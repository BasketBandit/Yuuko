package com.yuuko.core.commands.developer.commands;

import com.yuuko.core.Configuration;
import com.yuuko.core.commands.Command;
import com.yuuko.core.commands.developer.DeveloperModule;
import com.yuuko.core.events.entity.MessageEvent;

import java.net.URI;
import java.util.Arrays;

public class LavalinkNodeCommand extends Command {

    public LavalinkNodeCommand() {
        super("lavalink", DeveloperModule.class, 2, Arrays.asList("-lavalink <action> <node> <secret>"), false, null);
    }

    @Override
    public void onCommand(MessageEvent e) {
        try {
            String[] commandParameters = e.getCommand().get(1).split("\\s+", 3);
            if(commandParameters[0].equals("add")) {
                Configuration.LAVALINK.getLavalink().addNode(URI.create(commandParameters[1]), commandParameters[2]);
            } else if(commandParameters[0].equals("remove")) {
                Configuration.LAVALINK.getLavalink().removeNode(Integer.parseInt(commandParameters[1]));
            }
        } catch(Exception ex) {
            log.error("An error occurred while running the {} class, message: {}", this, ex.getMessage(), ex);
        }
    }
}
