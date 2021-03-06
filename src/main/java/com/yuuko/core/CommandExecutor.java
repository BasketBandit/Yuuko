package com.yuuko.core;

import com.yuuko.core.commands.Command;
import com.yuuko.core.commands.Module;
import com.yuuko.core.commands.audio.handlers.AudioManager;
import com.yuuko.core.commands.core.commands.BindCommand;
import com.yuuko.core.commands.core.commands.ModuleCommand;
import com.yuuko.core.database.function.GuildFunctions;
import com.yuuko.core.events.entity.MessageEvent;
import com.yuuko.core.utilities.TextUtilities;
import com.yuuko.core.utilities.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

public class CommandExecutor {
    private static final Logger log = LoggerFactory.getLogger(CommandExecutor.class);

    private static final List<String> constants = Arrays.asList("core", "developer", "setting");
    private static final List<String> disconnectedCommands = Arrays.asList("play", "playnext", "search", "background", "lyrics");
    private static final List<String> notInVoiceCommands = Arrays.asList("lyrics", "current", "last", "queue");
    private static final List<String> nonDJModeCommands = Arrays.asList("queue", "current", "last", "lyrics");
    private static final List<String> requiresDJ = Arrays.asList("play", "playnext", "clear", "background", "loop", "pause", "search", "seek", "shuffle", "skip", "stop");

    private MessageEvent event;
    private Module module;
    private Command command;
    private Member bot;
    private Member commander;

    public CommandExecutor(MessageEvent event) {
        // Is the event null? (Runtime setup for module reflection)
        if(event == null) {
            return;
        }

        this.event = event;
        this.module = event.getModule();
        this.command = event.getCommand();
        this.bot = event.getGuild().getSelfMember();
        this.commander = event.getMember();

        // Is the module enabled and does the command pass the binding checks?
        // Is module named "audio" and if so, does the user fail any of the checks?
        // Is the command on cooldown?
        if(command == null || !isEnabled() || isBound() || !isValidAudio() || !command.isCooling(event)) {
            return;
        }

        // Is the command or module NSFW? If they are, is the channel they're being used in /not/ NSFW?
        if(command.isNSFW() && !event.getChannel().isNSFW()) {
            EmbedBuilder embed = new EmbedBuilder().setTitle("Invalid Channel").setDescription("That command can only be used in NSFW marked channels.");
            MessageDispatcher.reply(event, embed.build());
            return;
        }

        // Does the command have any permissions?
        if(command.getPermissions() != null) {
            // Does the bot have the permission?
            if(!bot.hasPermission(command.getPermissions()) && !bot.hasPermission(event.getChannel(), command.getPermissions())) {
                EmbedBuilder embed = new EmbedBuilder().setTitle("Missing Permission").setDescription("I require the `" + Utilities.getCommandPermissions(command.getPermissions()) + "` permissions to use that command.");
                MessageDispatcher.reply(event, embed.build());
                return;
            }

            // Does the user have the permission?
            if(!commander.hasPermission(command.getPermissions()) && !commander.hasPermission(event.getChannel(), command.getPermissions())) {
                EmbedBuilder embed = new EmbedBuilder().setTitle("Missing Permission").setDescription("You require the `" + Utilities.getCommandPermissions(command.getPermissions()) + "` permissions to use that command.");
                MessageDispatcher.reply(event, embed.build());
                return;
            }
        }

        try {
            log.trace("Invoking {}#onCommand()", command.getClass().getSimpleName());
            command.onCommand(event);
            messageCleanup();
        } catch(Exception ex) {
            log.error("Something went wrong when executing the {} , message: {}", command.getClass().getSimpleName(), ex.getMessage(), ex);
            event.getMessage().addReaction("❌").queue();
        }
    }

    /**
     * Checks various conditions to see if using certain audio commands are appropriate for the context of the user. Also checks the DJ Mode setting.
     *
     * @return boolean
     */
    private boolean isValidAudio() {
        // If the module isn't the audio module, pass it through.
        if(!module.getName().equals("audio")) {
            return true;
        }

        // Is the member not in a voice channel?
        if(!commander.getVoiceState().inVoiceChannel() && !notInVoiceCommands.contains(command.getName())) {
            EmbedBuilder embed = new EmbedBuilder().setTitle("This command can only be used while in a voice channel.");
            MessageDispatcher.reply(event, embed.build());
            return false;
        }

        // Does a Lavalink link exist, if so, is the link disconnected and does the command require it to be otherwise?
        if(AudioManager.hasLink(event.getGuild()) && !AudioManager.isLinkConnected(event.getGuild()) && !disconnectedCommands.contains(command.getName())) {
            EmbedBuilder embed = new EmbedBuilder().setTitle("There is no active audio connection.");
            MessageDispatcher.reply(event, embed.build());
            return false;
        }

        // Are there any nodes available?
        if(AudioManager.LAVALINK.getLavalink().getNodes().size() < 1) {
            EmbedBuilder embed = new EmbedBuilder().setTitle("There are no Lavalink nodes available to handle your request.");
            MessageDispatcher.reply(event, embed.build());
            return false;
        }

        // Is the member an administrator?
        if(commander.hasPermission(Permission.ADMINISTRATOR)) {
            return true;
        }

        // Is DJ mode on, if yes does the member lack the DJ role, and if not is the command a DJ mode command?
        if(TextUtilities.toBoolean(GuildFunctions.getGuildSetting("djMode", event.getGuild().getId())) && requiresDJ.contains(command.getName()) && commander.getRoles().stream().noneMatch(role -> role.getName().equals("DJ"))) {
            EmbedBuilder embed = new EmbedBuilder().setTitle("DJ Mode Enabled").setDescription("While DJ mode is active, only a user with the role of 'DJ' can use that command.");
            MessageDispatcher.reply(event, embed.build());
            return false;
        }

        return true;
    }

    /**
     * Checks if either the module or command has been disabled.
     *
     * @return boolean
     */
    private boolean isEnabled() {
        // Executor still checks core/developer, in this case simply return false.
        if(constants.contains(module.getName())) {
            return true;
        }

        // Checks if the module is disabled.
        if(!ModuleCommand.DatabaseInterface.isEnabled(event.getGuild().getId(), module.getName())) {
            EmbedBuilder embed = new EmbedBuilder().setTitle("Module Disabled").setDescription("`" + module.getName() + "` module is disabled.");
            MessageDispatcher.reply(event, embed.build());
            return false;
        }

        if(!command.isEnabled()) {
            EmbedBuilder embed = new EmbedBuilder().setTitle("Command Disabled").setDescription("`" + command.getName() + "` command is disabled.");
            MessageDispatcher.reply(event, embed.build());
            return false;
        }

        return true;
    }

    /**
     * Checks channel bindings to see if commands are allowed to be executed there.
     *
     * @return boolean
     */
    private boolean isBound() {
        if(BindCommand.DatabaseInterface.isBound(event.getGuild().getId(), event.getChannel().getId(), module.getName())) {
            EmbedBuilder embed = new EmbedBuilder().setTitle("Module Bound").setDescription("The `" + command.getName() + "` command is bound to " + BindCommand.DatabaseInterface.getBindsByModule(event.getGuild(), module.getName(), ", ") + ".");
            MessageDispatcher.sendTempMessage(event, embed.build());
            return true;
        }

        return false;
    }

    /**
     * Removes the message that issued the command if the `deleteExecuted` setting is toggled to `on`.
     */
    private void messageCleanup() {
        // Does the server want the command message removed?
        if(TextUtilities.toBoolean(GuildFunctions.getGuildSetting("cleanupcommands", event.getGuild().getId()))) {
            if(bot.hasPermission(Permission.MESSAGE_MANAGE)) { // Can the bot manage messages?
                event.getMessage().delete().queue();
            } else {
                EmbedBuilder embed = new EmbedBuilder().setTitle("Missing Permission").setDescription("I am missing the `MESSAGE_MANAGE` permission required to execute the 'cleanupcommands' setting. If this setting is active by mistake, use `@Yuuko#2525 setting cleanupcommands false`.");
                MessageDispatcher.reply(event, embed.build());
            }
        }
    }

}