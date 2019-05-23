package com.yuuko.core.events.controllers;

import com.yuuko.core.commands.core.settings.StarboardSetting;
import com.yuuko.core.commands.utility.commands.ReactionRoleCommand;
import com.yuuko.core.metrics.handlers.MetricsManager;
import net.dv8tion.jda.core.events.message.react.GenericMessageReactionEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionRemoveEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GenericMessageReactionController {
    private static final Logger log = LoggerFactory.getLogger(GenericMessageReactionController.class);

    public GenericMessageReactionController(GenericMessageReactionEvent e) {
        if(e instanceof MessageReactionAddEvent || e instanceof MessageReactionRemoveEvent) {
            processGenericMessageReactionEvent(e);
        }
    }

    private void processGenericMessageReactionEvent(GenericMessageReactionEvent e) {
        try {
            if(e.getUser().isBot()) {
                MetricsManager.getEventMetrics().BOT_REACTS_PROCESSED.getAndIncrement();
                return;
            }

            MetricsManager.getEventMetrics().HUMAN_REACTS_PROCESSED.getAndIncrement();

            // Starboard
            if(e instanceof MessageReactionAddEvent && e.getReactionEmote().getName().equals("⭐")) {
                StarboardSetting.execute((MessageReactionAddEvent) e);
            }

            // Reaction Role
            ReactionRoleCommand.processReaction(e);

        } catch(Exception ex) {
            log.error("An error occurred while running the {} class, message: {}", this, ex.getMessage(), ex);
        }
    }

}
