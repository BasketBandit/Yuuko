package com.yuuko.core.commands.core.commands;

import com.yuuko.core.MessageDispatcher;
import com.yuuko.core.Yuuko;
import com.yuuko.core.commands.Command;
import com.yuuko.core.events.entity.MessageEvent;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.Arrays;

public class PermissionsCommand extends Command {

    public PermissionsCommand() {
        super("permissions", Yuuko.MODULES.get("core"), 0, -1L, Arrays.asList("-permissions"), false, null);
    }

    @Override
    public void onCommand(MessageEvent e) throws Exception {
        String permissions = e.getGuild().getSelfMember().getPermissions().toString().replace("[", "").replace("]", "").replace(",", "\n");
        EmbedBuilder about = new EmbedBuilder().setTitle("Permissions")
                .setDescription("One of the most common reasons for commands not to work is lack of required permissions. Below are all of the permissions that I current have.")
                .addField("Granted", permissions, true);
        MessageDispatcher.reply(e, about.build());
    }
}
