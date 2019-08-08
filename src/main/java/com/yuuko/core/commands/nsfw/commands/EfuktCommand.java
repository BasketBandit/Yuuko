package com.yuuko.core.commands.nsfw.commands;

import com.yuuko.core.Configuration;
import com.yuuko.core.MessageHandler;
import com.yuuko.core.commands.Command;
import com.yuuko.core.events.entity.MessageEvent;
import com.yuuko.core.io.RequestHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Arrays;

public class EfuktCommand extends Command {

    private static final String BASE_URL = "https://efukt.com/random.php";

    public EfuktCommand() {
        super("efukt", Configuration.MODULES.get("nsfw"), 0, Arrays.asList("-efukt"), true, null);
    }

    @Override
    public void onCommand(MessageEvent e) {
        try {
            Document doc = new RequestHandler(BASE_URL).getDocument();
            Elements meta = doc.getElementsByTag("meta");

            String image = "https://i.imgur.com/YXqsEo6.jpg";
            if(doc.baseUri().startsWith("https://efukt.com/view.gif.php")) {
                image = doc.getElementsByClass("image_content").attr("src");
            } else {
                for(Element tag: meta) {
                    if(tag.hasAttr("property") && tag.attr("property").equals("og:image")) {
                        image = tag.attr("content");
                    }
                }
            }

            EmbedBuilder efuktPost = new EmbedBuilder()
                    .setTitle(doc.title().substring(0, doc.title().length() < 256 ? doc.title().length() : 256))
                    .setDescription(doc.baseUri())
                    .setImage(image)
                    .setFooter(Configuration.STANDARD_STRINGS.get(1) + e.getMember().getEffectiveName(), e.getAuthor().getEffectiveAvatarUrl());
            MessageHandler.sendMessage(e, efuktPost.build());

        } catch(Exception ex) {
            log.error("An error occurred while running the {} class, message: {}", this, ex.getMessage(), ex);
        }
    }

}
