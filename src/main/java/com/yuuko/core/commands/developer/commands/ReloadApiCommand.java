package com.yuuko.core.commands.developer.commands;

import com.yuuko.core.Configuration;
import com.yuuko.core.MessageHandler;
import com.yuuko.core.api.ApiManager;
import com.yuuko.core.commands.Command;
import com.yuuko.core.events.entity.MessageEvent;
import net.dv8tion.jda.core.EmbedBuilder;

import java.util.Arrays;

public class ReloadApiCommand extends Command {

    public ReloadApiCommand() {
        super("reapi", Configuration.MODULES.get("developer"), 0, Arrays.asList("-reapi"), false, null);
    }

    @Override
    public void onCommand(MessageEvent e) {
        try {
            Configuration.API_MANAGER = new ApiManager();
            EmbedBuilder embed = new EmbedBuilder().setTitle("Successfully reloaded ApiManager.");
            MessageHandler.sendMessage(e, embed.build());
        } catch(Exception ex) {
            log.error("An error occurred while running the {} class, message: {}", this, ex.getMessage(), ex);
        }
    }
}
