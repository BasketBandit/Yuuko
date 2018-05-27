package basketbandit.core.modules.audio.commands;

import basketbandit.core.Configuration;
import basketbandit.core.modules.Command;
import basketbandit.core.modules.audio.ModuleAudio;
import basketbandit.core.modules.audio.handlers.AudioManagerHandler;
import basketbandit.core.modules.audio.handlers.GuildAudioManager;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.awt.*;

public class CommandLastTrack extends Command {

    public CommandLastTrack() {
        super("lasttrack", "basketbandit.core.modules.audio.ModuleAudio", null);
    }

    public CommandLastTrack(MessageReceivedEvent e) {
        super("lasttrack", "basketbandit.core.modules.audio.ModuleAudio", null);
        executeCommand(e);
    }

    /**
     * Executes command using MessageReceivedEvent e.
     * @param e; MessageReceivedEvent.
     * @return boolean; if the command executed correctly.
     */
    protected void executeCommand(MessageReceivedEvent e) {
        GuildAudioManager manager = AudioManagerHandler.getGuildAudioManager(e.getGuild().getId());
        AudioTrack track = manager.scheduler.getLastTrack();
        String[] uri = track.getInfo().uri.split("=");

        if(manager.player.getPlayingTrack() != null) {
            EmbedBuilder queuedTrack = new EmbedBuilder()
                    .setColor(Color.WHITE)
                    .setAuthor("Last Track")
                    .setTitle(track.getInfo().title, track.getInfo().uri)
                    .setThumbnail("https://img.youtube.com/vi/" + uri[1] + "/1.jpg")
                    .addField("Duration", ModuleAudio.getTimestamp(track.getDuration()), true)
                    .addField("Channel", track.getInfo().author, true)
                    .setFooter("Version: " + Configuration.VERSION + ", Requested by " + e.getMember().getEffectiveName(), e.getGuild().getMemberById(Configuration.BOT_ID).getUser().getAvatarUrl());

            e.getTextChannel().sendMessage(queuedTrack.build()).queue();
        } else {
            e.getTextChannel().sendMessage("There is no last track...").queue();
        }

    }
}
