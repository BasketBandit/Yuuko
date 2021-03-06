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

public class DanceCommand extends InteractionCommand {
    private static final List<String> interactionImage = Arrays.asList(
            "https://i.imgur.com/TwMOUGe.gif",
            "https://i.imgur.com/9uzmcJd.gif",
            "https://i.imgur.com/AgScXfC.gif",
            "https://i.imgur.com/pBm2v7f.gif",
            "https://i.imgur.com/KkdPUnz.gif"
    );

    public DanceCommand() {
        super("dance", Yuuko.MODULES.get("interaction"), 0, -1L, Arrays.asList("-dance", "-dance @user"), false, null);
    }

    @Override
    public void onCommand(MessageEvent e) throws Exception {
        if(MessageUtilities.checkIfUserMentioned(e)) {
            Member target = MessageUtilities.getMentionedMember(e, true);
            if(target != null) {
                EmbedBuilder embed = new EmbedBuilder().setDescription("**" + e.getMember().getEffectiveName() + "** dances with **" + target.getEffectiveName() + "**.").setImage(interactionImage.get(getRandom(interactionImage.size())));
                MessageDispatcher.sendMessage(e, embed.build());
            }
            return;
        }

        EmbedBuilder embed = new EmbedBuilder().setDescription("**" + e.getMember().getEffectiveName() + "** dances.").setImage(interactionImage.get(getRandom(interactionImage.size())));
        MessageDispatcher.sendMessage(e, embed.build());
    }
}
