package com.yuuko.core.commands.moderation.commands;

import com.yuuko.core.commands.Command;
import com.yuuko.core.commands.moderation.ModerationModule;
import com.yuuko.core.utilities.MessageHandler;
import com.yuuko.core.utilities.MessageUtility;
import com.yuuko.core.utilities.Sanitiser;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class BanCommand extends Command {

    public BanCommand() {
        super("ban", ModerationModule.class,1, new String[]{"-ban @user [days]", "-ban @user [days] [reason]"}, false, new Permission[]{Permission.BAN_MEMBERS});
    }

    @Override
    public void onCommand(MessageReceivedEvent e, String[] command) {
        String[] commandParameters = command[1].split("\\s+", 3);
        Member target;

        if(commandParameters[0].length() == 18 && Sanitiser.isNumber(commandParameters[0])) {
            target = e.getGuild().getMemberById(commandParameters[0]);
        } else {
            target = MessageUtility.getFirstMentionedMember(e);
        }

        if(target == null) {
            return;
        }

        final int time;

        if(Sanitiser.isNumber(commandParameters[1])) {
            time = Integer.parseInt(commandParameters[1]);
        } else {
            EmbedBuilder embed = new EmbedBuilder().setTitle("Invalid Input").setDescription("Time parameter '**" + commandParameters[1] + "**' is invalid, ban defaulted to 1 day.");
            MessageHandler.sendMessage(e, embed.build());
            time = 1;
        }

        if(commandParameters.length < 3) {
            e.getGuild().getController().ban(target, time).queue();
        } else {
            e.getGuild().getController().ban(target, time, commandParameters[2]).queue();
        }
    }

}
