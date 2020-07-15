package com.yuuko.core.utilities;

import com.yuuko.core.Configuration;
import com.yuuko.core.database.function.GuildFunctions;
import com.yuuko.core.metrics.MetricsManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.SelfUser;

import java.util.List;

public final class Utilities {

    /**
     * Updates stats for DiscordBotList
     */
    public static void updateDiscordBotList() {
        final int shardId = Configuration.SHARD_ID;
        final int shardCount = Configuration.SHARD_COUNT;
        final int guildCount = MetricsManager.getDiscordMetrics().GUILD_COUNT.get();

        try {
            if(Configuration.BOT_LIST != null) {
                Configuration.BOT_LIST.setStats(shardId, shardCount, guildCount);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns an API ApplicationId.
     *
     * @param key name of the api
     * @return String
     */
    public static String getApiApplicationId(String key) {
        return Configuration.API_MANAGER.getApi(key).getApplicationId();
    }

    /**
     * Returns an API key.
     *
     * @param key name of the api
     * @return String
     */
    public static String getApiKey(String key) {
        return Configuration.API_MANAGER.getApi(key).getKey();
    }

    /**
     * Returns the server custom prefix.
     *
     * @param guild {@link Guild}
     * @return String
     */
    public static String getServerPrefix(Guild guild) {
        return GuildFunctions.getGuildSetting("prefix", guild.getId());
    }

    /**
     * Returns a pretty version of a command's permission array by removing the brackets surrounding them.
     *
     * @param permissions {@link List<Permission>}
     * @return String
     */
    public static String getCommandPermissions(List<Permission> permissions) {
        return permissions.toString().replace("[", "").replace("]", "");
    }

    /**
     * Returns the specific shard's SelfUser object.
     *
     * @return {@link SelfUser}
     */
    public static SelfUser getSelfUser() {
        for(JDA shard : Configuration.SHARD_MANAGER.getShards()) {
            if(shard.getStatus().equals(JDA.Status.CONNECTED)) {
                return shard.getSelfUser();
            }
        }
        return null;
    }
}
