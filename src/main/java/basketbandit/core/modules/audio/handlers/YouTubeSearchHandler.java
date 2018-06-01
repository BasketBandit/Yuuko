package basketbandit.core.modules.audio.handlers;

import basketbandit.core.Configuration;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.io.IOException;
import java.util.List;

public class YouTubeSearchHandler {

    /**
     * Searches youtube using command[1] and returns the first result.
     * @return youtube video url.
     */
    public static String search(String searchParameter) {

        try {
            YouTube youtube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), request -> {
            }).setApplicationName("basketbandit-204012").build();

            YouTube.Search.List search = youtube.search().list("id,snippet");

            search.setKey(Configuration.GOOGLE_API);
            search.setQ(searchParameter);
            search.setType("video");
            search.setFields("items(id/videoId)");
            search.setMaxResults(1L);

            SearchListResponse searchResponse = search.execute();
            List<SearchResult> searchResultList = searchResponse.getItems();

            if(searchResultList.isEmpty()) {
                return null;
            }

            SearchResult result = searchResultList.get(0);
            return "https://www.youtube.com/watch?v=" + result.getId().getVideoId();

        } catch (GoogleJsonResponseException ex) {
            System.err.println("There was a service error: " + ex.getDetails().getCode() + " : " + ex.getDetails().getMessage());
            return null;
        } catch (IOException cx) {
            System.err.println("There was an IO error: " + cx.getCause());
            return null;
        }
    }

    /**
     * Searches youtube using command[1] and returns the first 10 result.
     * @return youtube video result list.
     */
    public static List<SearchResult> searchList(MessageReceivedEvent e) {
        String[] commandArray = e.getMessage().getContentRaw().split("\\s+",2);

        try {
            YouTube youtube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), request -> {
            }).setApplicationName("basketbandit-204012").build();
            YouTube.Search.List search = youtube.search().list("id,snippet");

            search.setKey(Configuration.GOOGLE_API);
            search.setQ(commandArray[1]);
            search.setType("video");
            search.setFields("items(id/videoId,snippet/title)");
            search.setMaxResults(10L);

            SearchListResponse searchResponse = search.execute();

            return searchResponse.getItems();

        } catch (GoogleJsonResponseException ex) {
            System.err.println("There was a service error: " + ex.getDetails().getCode() + " : " + ex.getDetails().getMessage());
            return null;
        } catch (IOException cx) {
            System.err.println("There was an IO error: " + cx.getCause() + " : " + e.getMessage());
            return null;
        }

    }
}
