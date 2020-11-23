package com.yuuko.core.commands.utility.commands;

import com.yuuko.core.Config;
import com.yuuko.core.MessageHandler;
import com.yuuko.core.commands.Command;
import com.yuuko.core.events.entity.MessageEvent;
import com.yuuko.core.utilities.TextUtilities;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Role;

import java.util.Arrays;

public class RolesCommand extends Command {

    public RolesCommand() {
        super("roles", Config.MODULES.get("utility"), 0, -1L, Arrays.asList("-roles"), false, null);
    }

    @Override
    public void onCommand(MessageEvent e) {
        StringBuilder roles = new StringBuilder();

        if(!e.getGuild().getRoleCache().isEmpty()) {
            int characterCount = 0;

            for(Role role : e.getGuild().getRoleCache()) {
                if(characterCount + role.getAsMention().length() + 2 < 2048) {
                    roles.append(role.getAsMention()).append("\n");
                    characterCount += role.getAsMention().length() + 1;
                }
            }
            TextUtilities.removeLast(roles, "\n");
        } else {
            roles.append("None Available");
        }

        EmbedBuilder embed = new EmbedBuilder()
                .setTitle(e.getGuild().getName() + " Roles")
                .setDescription(roles.toString())
                .setFooter(Config.STANDARD_STRINGS.get(1) + e.getMember().getEffectiveName(), e.getAuthor().getEffectiveAvatarUrl());
        MessageHandler.reply(e, embed.build());
    }
}
