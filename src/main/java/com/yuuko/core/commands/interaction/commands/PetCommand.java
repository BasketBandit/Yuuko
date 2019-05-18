package com.yuuko.core.commands.interaction.commands;

import com.yuuko.core.MessageHandler;
import com.yuuko.core.commands.Command;
import com.yuuko.core.commands.interaction.InteractionModule;
import com.yuuko.core.events.extensions.MessageEvent;
import com.yuuko.core.utilities.MessageUtilities;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class PetCommand extends Command {

    private static final List<String> interactionImage = Arrays.asList(
            "https://i.imgur.com/4ssddEQ.gif",
            "https://i.imgur.com/UWbKpx8.gif",
            "https://i.imgur.com/2k0MFIr.gif",
            "https://i.imgur.com/6xfbS1q.gif",
            "https://i.imgur.com/KRsZdho.gif"
    );

    public PetCommand() {
        super("pet", InteractionModule.class, 1, Arrays.asList("-pet @user"), false, null);
    }

    @Override
    public void onCommand(MessageEvent e) {
        Member target = MessageUtilities.getMentionedMember(e, true);
        if(target != null) {
            EmbedBuilder embed = new EmbedBuilder().setDescription("**" + e.getMember().getEffectiveName() + "** pets **" + target.getEffectiveName() + "**.").setImage(interactionImage.get(new Random().nextInt(interactionImage.size() -1)));
            MessageHandler.sendMessage(e, embed.build());
        }
    }
}
