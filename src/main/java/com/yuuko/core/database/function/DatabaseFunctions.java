package com.yuuko.core.database.function;

import com.yuuko.core.Configuration;
import com.yuuko.core.database.connection.DatabaseConnection;
import com.yuuko.core.entity.Shard;
import com.yuuko.core.metrics.MetricsManager;
import com.yuuko.core.metrics.pathway.AudioMetrics;
import com.yuuko.core.metrics.pathway.CacheMetrics;
import com.yuuko.core.metrics.pathway.DiscordMetrics;
import com.yuuko.core.metrics.pathway.SystemMetrics;
import net.dv8tion.jda.api.JDA;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class DatabaseFunctions {
    private static final Logger log = LoggerFactory.getLogger(DatabaseFunctions.class);
    private static final SystemMetrics system = MetricsManager.getSystemMetrics();
    private static final DiscordMetrics discord = MetricsManager.getDiscordMetrics();
    private static final AudioMetrics audio = MetricsManager.getAudioMetrics();
    private static final CacheMetrics cache = MetricsManager.getCacheMetrics();

    /**
     * Updates the database with the latest metrics.
     */
    public static void updateMetricsDatabase() {
        try(Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO `system_metrics`(`shardId`, `uptime`, `memoryTotal`, `memoryUsed`) VALUES(?, ?, ?, ?)");
            PreparedStatement stmt2 = conn.prepareStatement("INSERT INTO `discord_metrics`(`shardId`, `gatewayPing`, `restPing`, `guildCount`) VALUES(?, ?, ?, ?)");
            PreparedStatement stmt3 = conn.prepareStatement("INSERT INTO `audio_metrics`(`players`, `activePlayers`, `queueSize`) VALUES(?, ?, ?)");
            PreparedStatement stmt4 = conn.prepareStatement("INSERT INTO `cache_metrics`(`trackIdMatch`, `trackIdSize`) VALUES(?, ?)")) {

            int shardId = Configuration.BOT.getJDA().getShardInfo().getShardId();

            stmt.setInt(1, shardId);
            stmt.setLong(2, system.UPTIME);
            stmt.setLong(3, system.MEMORY_TOTAL);
            stmt.setLong(4, system.MEMORY_USED);
            stmt.execute();

            stmt2.setInt(1, shardId);
            stmt2.setDouble(2, discord.GATEWAY_PING.get());
            stmt2.setDouble(3, discord.REST_PING.get());
            stmt2.setInt(4, discord.GUILD_COUNT.get());
            stmt2.execute();

            stmt3.setInt(1, audio.PLAYERS_TOTAL.get());
            stmt3.setInt(2, audio.PLAYERS_ACTIVE.get());
            stmt3.setInt(3, audio.QUEUE_SIZE.get());
            stmt3.execute();

            stmt4.setInt(1, cache.TRACK_ID_CACHE_HITS.get());
            stmt4.setInt(2, cache.TRACK_ID_CACHE_SIZE.get());
            stmt4.execute();

        } catch(Exception ex) {
            log.error("An error occurred while running the {} class, message: {}", DatabaseFunctions.class.getSimpleName(), ex.getMessage(), ex);
        }
    }

    /**
     * Updates the database with the latest command.
     *
     * @param guildId String
     * @param command String
     * @param executionTime double (milliseconds)
     */
    public static void updateCommandLog(String guildId, String command, double executionTime) {
        try(Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO `command_log`(`shardId`, `guildId`, `command`, `executionTime`) VALUES(?, ?, ?, ?)")) {

            stmt.setInt(1, Configuration.BOT.getJDA().getShardInfo().getShardId());
            stmt.setString(2, guildId);
            stmt.setString(3, command);
            stmt.setDouble(4, executionTime);
            stmt.execute();

        } catch (Exception ex) {
            log.error("An error occurred while running the {} class, message: {}", DatabaseFunctions.class.getSimpleName(), ex.getMessage(), ex);
        }
    }

    /**
     * Truncates the metrics database. (This happens when the bot is first loaded.)
     *
     * @param shard int
     */
    public static void truncateMetrics(int shard) {
        try(Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement("DELETE FROM `system_metrics` WHERE shardId = ?");
            PreparedStatement stmt2 = conn.prepareStatement("DELETE FROM `discord_metrics` WHERE shardId = ?");
            PreparedStatement stmt3 = conn.prepareStatement("DELETE FROM `audio_metrics`");
            PreparedStatement stmt4 = conn.prepareStatement("DELETE FROM `command_log` WHERE shardId = ?");
            PreparedStatement stmt5 = conn.prepareStatement("DELETE FROM `cache_metrics`")) {

            stmt.setInt(1, shard);
            stmt.execute();

            stmt2.setInt(1, shard);
            stmt2.execute();

            stmt3.execute();

            stmt4.setInt(1, shard);
            stmt4.execute();

            stmt5.execute();

        } catch(Exception ex) {
            log.error("An error occurred while running the {} class, message: {}", DatabaseFunctions.class.getSimpleName(), ex.getMessage(), ex);
        }
    }

    /**
     * Truncates anything 6 hours old in the system and discord metrics. Others are kept for total values.
     */
    public static void pruneMetrics() {
        try(Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement("DELETE FROM `system_metrics` WHERE dateInserted < DATE_SUB(NOW(), INTERVAL 6 HOUR);");
            PreparedStatement stmt2 = conn.prepareStatement("DELETE FROM `discord_metrics` WHERE dateInserted < DATE_SUB(NOW(), INTERVAL 6 HOUR);");
            PreparedStatement stmt3 = conn.prepareStatement("DELETE FROM `audio_metrics` WHERE dateInserted < DATE_SUB(NOW(), INTERVAL 6 HOUR);");
            PreparedStatement stmt4 = conn.prepareStatement("DELETE FROM `cache_metrics` WHERE dateInserted < DATE_SUB(NOW(), INTERVAL 6 HOUR);")) {

            stmt.execute();
            stmt2.execute();
            stmt3.execute();
            stmt4.execute();

        } catch(Exception ex) {
            log.error("An error occurred while running the {} class, message: {}", DatabaseFunctions.class.getSimpleName(), ex.getMessage(), ex);
        }
    }

    /**
     * Updates settings from channels that are deleted.
     *
     * @param setting the setting to cleanup.
     * @param guildId the guild to cleanup.
     */
    public static void cleanupSettings(String setting, String guildId) {
        try(Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement("UPDATE `guilds_settings` SET " + setting + " = null WHERE `guildId` = ?")){

            stmt.setString(1, guildId);
            stmt.execute();

        } catch(Exception ex) {
            log.error("An error occurred while running the {} class, message: {}", DatabaseFunctions.class.getSimpleName(), ex.getMessage(), ex);
        }
    }

    /**
     * Queries the provisioning database and supplies the next available shard ID.
     *
     * @return the next available shard ID, starting at 0.
     */
    public static int provideShardId() {
        try(Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM `shards` ORDER BY `shardId`");
            PreparedStatement stmt2 = conn.prepareStatement("INSERT INTO `shards`(`shardId`) VALUES(?)")){

            ResultSet resultSet = stmt.executeQuery();

            int shard = 0;
            while(resultSet.next()) {
                if(resultSet.getInt(1) == shard) {
                    shard++;
                }
            }

            stmt2.setInt(1, shard);
            stmt2.execute();

            return shard;

        } catch(Exception ex) {
            log.error("An error occurred while running the {} class, message: {}", DatabaseFunctions.class.getSimpleName(), ex.getMessage(), ex);
            return -1;
        }
    }

    /**
     * Retrieves the total shard count from the database.
     *
     * @return the total shard count expected from the database.
     */
    public static int getShardCount() {
        try(Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM `shard_configuration`")){

            ResultSet resultSet = stmt.executeQuery();
            if(resultSet.next()) {
                return resultSet.getInt("ShardCount");
            } else {
                return -1;
            }

        } catch(Exception ex) {
            log.error("An error occurred while running the {} class, message: {}", DatabaseFunctions.class.getSimpleName(), ex.getMessage(), ex);
            return -1;
        }
    }

    /**
     * Update shard statistics such as guild count.
     */
    public static void updateShardStatistics() {
        try(Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement("UPDATE `shards` SET `status` = ?, `guilds` = ?, `gatewayPing` = ?, `restPing` = ?, `shardAssigned` = CURRENT_TIMESTAMP  WHERE `shardId` = ?")){

            JDA shard = Configuration.BOT.getJDA();

            stmt.setString(1, shard.getStatus().name());
            stmt.setInt(2, discord.GUILD_COUNT.get());
            stmt.setInt(3, discord.GATEWAY_PING.get());
            stmt.setInt(4, discord.REST_PING.get());
            stmt.setInt(5, shard.getShardInfo().getShardId());
            stmt.execute();


        } catch(Exception ex) {
            log.error("An error occurred while running the {} class, message: {}", DatabaseFunctions.class.getSimpleName(), ex.getMessage(), ex);
        }
    }

    /**
     * Retrieves shard statistics from the database.
     *
     * @return ArrayList<Shard> of shard statistics.
     */
    public static ArrayList<Shard> getShardStatistics() {
        try(Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM `shards`")){

            ArrayList<Shard> shards = new ArrayList<>();

            ResultSet resultSet = stmt.executeQuery();
            while(resultSet.next()) {
                shards.add(new Shard(resultSet.getInt(1), resultSet.getString(2), resultSet.getInt(4), resultSet.getInt(5)));
            }

            return shards;

        } catch(Exception ex) {
            log.error("An error occurred while running the {} class, message: {}", DatabaseFunctions.class.getSimpleName(), ex.getMessage(), ex);
            return new ArrayList<>();
        }
    }

    /**
     * Clears the previsioning database of expired IDs (Older than 35 seconds).
     */
    public static void pruneExpiredShards() {
        try(Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement("DELETE FROM `shards` WHERE `shardAssigned` < DATE_SUB(NOW(), INTERVAL 31 SECOND)")) {

            stmt.execute();

        } catch(Exception ex) {
            log.error("An error occurred while running the {} class, message: {}", DatabaseFunctions.class.getSimpleName(), ex.getMessage(), ex);
        }
    }
}
