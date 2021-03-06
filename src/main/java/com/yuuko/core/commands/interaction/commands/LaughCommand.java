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

public class LaughCommand extends InteractionCommand {
    private static final List<String> interactionImage = Arrays.asList(
            "https://i.imgur.com/SGboaP0.gif",
            "https://i.imgur.com/S0m2mfm.gif",
            "https://i.imgur.com/12T0WK1.gif",
            "https://i.imgur.com/1i53Pu5.gif",
            "https://i.imgur.com/EgOdPmj.gif"
    );

    public LaughCommand() {
        super("laugh", Yuuko.MODULES.get("interaction"), 0, -1L, Arrays.asList("-laugh", "-laugh @user"), false, null);
    }

    @Override
    public void onCommand(MessageEvent e) throws Exception {
        if(MessageUtilities.checkIfUserMentioned(e)) {
            Member target = MessageUtilities.getMentionedMember(e, true);
            if(target != null) {
                EmbedBuilder embed = new EmbedBuilder().setDescription("**" + e.getMember().getEffectiveName() + "** laughs at **" + target.getEffectiveName() + "**.").setImage(interactionImage.get(getRandom(interactionImage.size())));
                MessageDispatcher.sendMessage(e, embed.build());
            }
            return;
        }

        EmbedBuilder embed = new EmbedBuilder().setDescription("**" + e.getMember().getEffectiveName() + "** laughs.").setImage(interactionImage.get(getRandom(interactionImage.size())));
        MessageDispatcher.sendMessage(e, embed.build());
    }
}
