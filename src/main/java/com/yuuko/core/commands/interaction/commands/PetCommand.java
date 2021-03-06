package com.yuuko.core.commands.interaction.commands;

import com.yuuko.core.MessageDispatcher;
import com.yuuko.core.Yuuko;
import com.yuuko.core.commands.interaction.InteractionCommand;
import com.yuuko.core.events.entity.MessageEvent;
import com.yuuko.core.utilities.MessageUtilities;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;

import java.util.Arrays;
import java.util.List;

public class PetCommand extends InteractionCommand {
    private static final List<String> interactionImage = Arrays.asList(
            "https://i.imgur.com/4ssddEQ.gif",
            "https://i.imgur.com/UWbKpx8.gif",
            "https://i.imgur.com/2k0MFIr.gif",
            "https://i.imgur.com/6xfbS1q.gif",
            "https://i.imgur.com/KRsZdho.gif"
    );

    public PetCommand() {
        super("pet", Yuuko.MODULES.get("interaction"), 1, -1L, Arrays.asList("-pet @user"), false, null);
    }

    @Override
    public void onCommand(MessageEvent e) throws Exception {
        Member target = MessageUtilities.getMentionedMember(e, true);
        if(target != null) {
            EmbedBuilder embed = new EmbedBuilder().setDescription("**" + e.getMember().getEffectiveName() + "** pets **" + target.getEffectiveName() + "**.").setImage(interactionImage.get(getRandom(interactionImage.size())));
            MessageDispatcher.sendMessage(e, embed.build());
        }
    }
}
