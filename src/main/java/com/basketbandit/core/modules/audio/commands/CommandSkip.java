package com.basketbandit.core.modules.audio.commands;

import com.basketbandit.core.modules.Command;
import com.basketbandit.core.modules.audio.handlers.AudioManagerHandler;
import com.basketbandit.core.modules.audio.handlers.GuildAudioManager;
import com.basketbandit.core.utils.Utils;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class CommandSkip extends Command {

    public CommandSkip() {
        super("skip", "com.basketbandit.core.modules.audio.ModuleAudio", new String[]{"-skip"}, null);
    }

    public CommandSkip(MessageReceivedEvent e, String[] command) {
        executeCommand(e, command);
    }

    @Override
    protected void executeCommand(MessageReceivedEvent e, String[] command) {
        GuildAudioManager manager = AudioManagerHandler.getGuildAudioManager(e.getGuild().getId());

        try {
            if(manager.scheduler.hasNextTrack()) {
                // Send the message before skipping the track or the track metadata becomes unavailable, causing a null pointer.
                Utils.sendMessage(e, e.getAuthor().getAsMention() + " skipped track: " + manager.player.getPlayingTrack().getInfo().title);
                manager.scheduler.nextTrack();
            } else {
                Utils.sendMessage(e, "The queue is empty, there is no track to skip!");
            }

        } catch(Exception ex) {
            Utils.sendException(ex, e.getMessage().getContentRaw());
        }
    }

}
