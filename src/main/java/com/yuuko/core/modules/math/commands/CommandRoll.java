package com.yuuko.core.modules.math.commands;

import com.yuuko.core.modules.Command;
import com.yuuko.core.utils.MessageHandler;
import com.yuuko.core.utils.Sanitiser;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.Random;

public class CommandRoll extends Command {

    public CommandRoll() {
        super("roll", "com.yuuko.core.modules.math.ModuleMath", 1, new String[]{"-roll [number]", "-roll 00"}, null);
    }

    @Override
    public void executeCommand(MessageReceivedEvent e, String[] command) {

        int rollNum;
        if(Sanitiser.isNumber(command[1])) {
            rollNum = Integer.parseInt(command[1]);
        } else {
            EmbedBuilder embed = new EmbedBuilder().setTitle("Invalid Input.").setDescription("Input must be a non-negative numeric value.");
            MessageHandler.sendMessage(e, embed.build());
            return;
        }

        int num;
        if(command[1].equals("00")) {
            num = (new Random().nextInt(10) + 1) * 10;
        } else {
            num = new Random().nextInt(rollNum) + 1;
        }

        EmbedBuilder embed = new EmbedBuilder().setTitle(e.getAuthor().getName() + " rolled a " + num + ".", e.getAuthor().getAvatarUrl());
        MessageHandler.sendMessage(e, embed.build());

    }

}
