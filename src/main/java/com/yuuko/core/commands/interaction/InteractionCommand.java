package com.yuuko.core.commands.interaction;

import com.yuuko.core.commands.Command;
import com.yuuko.core.commands.Module;
import com.yuuko.core.events.entity.MessageEvent;
import net.dv8tion.jda.api.Permission;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public abstract class InteractionCommand extends Command {

    public InteractionCommand(String name, Module module, int expectedParameters, long cooldownDuration, List<String> usage, boolean nsfw, List<Permission> permissions) {
        super(name, module, expectedParameters, cooldownDuration, usage, nsfw, permissions);
    }

    @Override
    public abstract void onCommand(MessageEvent e) throws Exception;

    public int getRandom(int bound) {
        return ThreadLocalRandom.current().nextInt(bound);
    }
}
