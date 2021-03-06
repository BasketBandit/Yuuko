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

public class AngryCommand extends InteractionCommand {
    private static final List<String> interactionImage = Arrays.asList(
            "https://i.imgur.com/BLGBP1f.gif",
            "https://i.imgur.com/fWh43XO.gif",
            "https://i.imgur.com/T8vX3hy.gif",
            "https://i.imgur.com/20064tE.gif",
            "https://i.imgur.com/bTJofwO.gif"
    );

    public AngryCommand() {
        super("angry", Yuuko.MODULES.get("interaction"), 0, -1L, Arrays.asList("-angry", "-angry @user"), false, null);
    }

    @Override
    public void onCommand(MessageEvent e) throws Exception {
        if(MessageUtilities.checkIfUserMentioned(e)) {
            Member target = MessageUtilities.getMentionedMember(e, true);
            if(target != null) {
                EmbedBuilder embed = new EmbedBuilder().setDescription("**" + e.getMember().getEffectiveName() + "** is angry at **" + target.getEffectiveName() + "**.").setImage(interactionImage.get(getRandom(interactionImage.size())));
                MessageDispatcher.sendMessage(e, embed.build());
            }
            return;
        }

        EmbedBuilder embed = new EmbedBuilder().setDescription("**" + e.getMember().getEffectiveName() + "** is angry.").setImage(interactionImage.get(getRandom(interactionImage.size())));
        MessageDispatcher.sendMessage(e, embed.build());
    }
}
