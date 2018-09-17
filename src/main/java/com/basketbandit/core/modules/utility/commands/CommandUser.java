package com.basketbandit.core.modules.utility.commands;

import com.basketbandit.core.Configuration;
import com.basketbandit.core.modules.Command;
import com.basketbandit.core.utils.Utils;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class CommandUser extends Command {

    public CommandUser() {
        super("user", "com.basketbandit.core.modules.utility.ModuleUtility", new String[]{"-user @user"}, null);
    }

    public CommandUser(MessageReceivedEvent e, String[] command) {
        executeCommand(e, command);
    }

    @Override
    protected void executeCommand(MessageReceivedEvent e, String[] command) {
        List<Member> mentioned = e.getMessage().getMentionedMembers();
        Member target;

        if(mentioned.size() > 0) {
            target = mentioned.get(0);
        } else {
            target = e.getGuild().getMemberById(Long.parseLong(command[1]));
        }

        if(target == null) {
            Utils.sendMessage(e, "Sorry, that user could not be found.");
            return;
        }

        // Gets user's roles, replaces the last comma with nothing.
        List<Role> infoRoles = target.getRoles();
        StringBuilder roleString = new StringBuilder();

        for(Role role : infoRoles) {
            roleString.append(role.getName()).append(", ");
        }

        if(!roleString.toString().equals("")) {
            int index = roleString.lastIndexOf(", ");
            roleString = new StringBuilder(new StringBuilder(roleString.toString()).replace(index, index + 1, "").toString());
        }

        EmbedBuilder commandInfo = new EmbedBuilder()
                .setColor(Color.RED)
                .setAuthor("User information about " + target.getEffectiveName() + ",", null, target.getUser().getAvatarUrl())
                .setTitle("User is currently "+target.getOnlineStatus())
                .setThumbnail(target.getUser().getAvatarUrl())
                .addField("Username", Utils.getTag(target), true)
                .addField("User ID", target.getUser().getIdLong()+"", true)
                .addField("Account Created", target.getUser().getCreationTime().format(DateTimeFormatter.ofPattern("d MMM yyyy  hh:mma")), true)
                .addField("Joined Server", target.getJoinDate().format(DateTimeFormatter.ofPattern("d MMM yyyy  hh:mma")), true)
                .addField("Mutual Servers", target.getUser().getMutualGuilds().size()+"", true)
                .addField("Roles", roleString.toString(), true)
                .setFooter("Version: " + Configuration.VERSION + ", Information requested by " + e.getMember().getEffectiveName(), e.getGuild().getMemberById(Configuration.BOT_ID).getUser().getAvatarUrl());

        Utils.sendMessage(e, commandInfo.build());
    }
}
