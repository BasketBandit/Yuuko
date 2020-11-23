package com.yuuko.core.commands.audio.commands;

import com.yuuko.core.Config;
import com.yuuko.core.MessageHandler;
import com.yuuko.core.commands.Command;
import com.yuuko.core.commands.audio.handlers.AudioManagerController;
import com.yuuko.core.commands.audio.handlers.GuildAudioManager;
import com.yuuko.core.events.entity.MessageEvent;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.Arrays;

public class LoopCommand extends Command {

    public LoopCommand() {
        super("loop", Config.MODULES.get("audio"), 0, -1L, Arrays.asList("-loop"), false, null);
    }

    @Override
    public void onCommand(MessageEvent e) {
        GuildAudioManager manager = AudioManagerController.getGuildAudioManager(e.getGuild());

        EmbedBuilder embed = new EmbedBuilder().setTitle("Loop").setDescription("Looping for queue set to `" + !manager.getScheduler().isLooping() + "`");
        MessageHandler.reply(e, embed.build());
        manager.getScheduler().setLooping(!manager.getScheduler().isLooping());
    }

}
