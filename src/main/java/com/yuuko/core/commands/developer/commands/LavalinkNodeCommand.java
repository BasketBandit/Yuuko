package com.yuuko.core.commands.developer.commands;

import com.yuuko.core.Yuuko;
import com.yuuko.core.commands.Command;
import com.yuuko.core.commands.audio.handlers.AudioManager;
import com.yuuko.core.events.entity.MessageEvent;

import java.net.URI;
import java.util.Arrays;

public class LavalinkNodeCommand extends Command {

    public LavalinkNodeCommand() {
        super("lavalink", Yuuko.MODULES.get("developer"), 2, -1L, Arrays.asList("-lavalink <action> <node> <secret>"), false, null);
    }

    @Override
    public void onCommand(MessageEvent e) throws Exception {
        String[] params = e.getParameters().split("\\s+", 3);
        switch(params[0]) {
            case "add" -> AudioManager.LAVALINK.getLavalink().addNode(URI.create(params[1]), params[2]);
            case "remove" -> AudioManager.LAVALINK.getLavalink().removeNode(Integer.parseInt(params[1]));
            default -> {}
        }
    }
}
