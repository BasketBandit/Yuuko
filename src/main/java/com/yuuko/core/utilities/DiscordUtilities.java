package com.yuuko.core.utilities;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;

import java.util.List;

public final class DiscordUtilities {

    /**
     * Returns a username and discriminator in format username#discriminator.
     *
     * @param member {@link Member}
     * @return username#discriminator
     */
    public static String getTag(Member member) {
        return getTag(member.getUser());
    }

    /**
     * Returns a username and discriminator in format username#discriminator.
     *
     * @param user {@link User}
     * @return username#discriminator
     */
    public static String getTag(User user) {
        return user.getName() + "#" + user.getDiscriminator();
    }

    /**
     * Creates the muted role to correctly mute people.
     *
     * @param guild {@link Guild}
     * @return Role
     */
    public static Role getOrSetupMutedRole(Guild guild) {
        List<TextChannel> channels = guild.getTextChannels();
        List<VoiceChannel> voiceChannels = guild.getVoiceChannels();

        for(Role role: guild.getRoles()) {
            if(role.getName().equals("Muted")) {
                if(!guild.getSelfMember().canInteract(role)) {
                    return null;
                }
                return role;
            }
        }



        // max number of roles in guild is 250
        if(!guild.getSelfMember().hasPermission(Permission.MANAGE_ROLES) || guild.getRoleCache().size() == 250) {
            return null;
        }

        Role muted = guild.createRole().setName("Muted").complete();
        for(TextChannel channel: channels) {
            channel.createPermissionOverride(muted).setDeny(Permission.MESSAGE_WRITE, Permission.MESSAGE_ADD_REACTION).queue();
        }
        for(VoiceChannel channel: voiceChannels) {
            channel.createPermissionOverride(muted).setDeny(Permission.VOICE_CONNECT, Permission.VOICE_SPEAK).queue();
        }

        return muted;
    }
}
