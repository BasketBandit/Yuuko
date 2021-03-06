package com.yuuko.core.commands.media.commands;

import com.google.gson.JsonObject;
import com.yuuko.core.MessageDispatcher;
import com.yuuko.core.Yuuko;
import com.yuuko.core.commands.Command;
import com.yuuko.core.events.entity.MessageEvent;
import com.yuuko.core.io.RequestHandler;
import com.yuuko.core.io.entity.RequestProperty;
import com.yuuko.core.utilities.Sanitiser;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.Arrays;

public class AnimeCommand extends Command {
    private static final String BASE_URL = "https://kitsu.io/api/edge/anime?filter[text]=";

    public AnimeCommand() {
        super("anime", Yuuko.MODULES.get("media"), 1, -1L, Arrays.asList("-anime <title>"), false, null);
    }

    @Override
    public void onCommand(MessageEvent e) throws Exception {
        final String url = BASE_URL + Sanitiser.scrub(e.getParameters(), true) + "&page[limit]=1";
        JsonObject json = new RequestHandler(url, new RequestProperty("Accept", "application/vnd.api+json"), new RequestProperty("Content-Type","application/vnd.api+json")).getJsonObject();
        if(json == null || json.isJsonNull() || json.getAsJsonArray("data").size() < 1) {
            EmbedBuilder embed = new EmbedBuilder().setTitle("No Results").setDescription("Search for `" + e.getParameters() + "` produced no results.");
            MessageDispatcher.reply(e, embed.build());
            return;
        }

        JsonObject data = json.getAsJsonArray("data").get(0).getAsJsonObject().get("attributes").getAsJsonObject(); // It's important to find the item in the array where the data is stored.
        final String ageRating = data.get("ageRating").isJsonNull() ? "Unknown" : data.get("ageRating").getAsString() + ": " + data.get("ageRatingGuide").getAsString();
        final String episodes = data.get("episodeCount").isJsonNull() ? "Unknown" : data.get("episodeCount").getAsString();
        final String episodeLength = data.get("episodeLength").isJsonNull() ? "Unknown" : data.get("episodeLength").getAsString() + " minutes";
        final String totalLength = (data.get("episodeLength").isJsonNull() || data.get("episodeCount").isJsonNull()) ? "Unknown" : ((data.get("episodeLength").getAsInt()*data.get("episodeCount").getAsInt())/60) + " hours";
        final String type = data.get("showType").isJsonNull() ? "Unknown" : data.get("showType").getAsString();
        final String approvalRating = data.get("averageRating").isJsonNull() ? "Unknown" : data.get("averageRating").getAsString() + "%";
        final String status = data.get("status").isJsonNull() ? "Unknown" : data.get("status").getAsString();
        final String startDate = data.get("startDate").isJsonNull() ? "Unknown" : data.get("startDate").getAsString();
        final String endDate = data.get("endDate").isJsonNull() ? "Unknown" : data.get("endDate").getAsString();

        EmbedBuilder embed = new EmbedBuilder()
                .setTitle(data.get("canonicalTitle").getAsString() + " | " + data.get("titles").getAsJsonObject().get("ja_jp").getAsString(), data.get("youtubeVideoId").isJsonNull() ? "" : "https://www.youtube.com/watch?v=" + data.get("youtubeVideoId").toString())
                .setImage(data.get("posterImage").getAsJsonObject().get("medium").getAsString())
                .setDescription(data.get("synopsis").getAsString())
                .addField("Age Rating", ageRating, true)
                .addField("Episodes", episodes, true)
                .addField("Episode Length", episodeLength, true)
                .addField("Total Length", totalLength, true)
                .addField("Type", type, true)
                .addField("Kitsu Approval Rating", approvalRating, true)
                .addField("Status", status, true)
                .addField("Start Date", startDate, true)
                .addField("End Date", endDate, true)
                .setFooter(Yuuko.STANDARD_STRINGS.get(1) + e.getAuthor().getAsTag(), e.getAuthor().getEffectiveAvatarUrl());
        MessageDispatcher.reply(e, embed.build());
    }

}
