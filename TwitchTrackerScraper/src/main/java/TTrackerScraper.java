import java.net.URI;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.io.IOException;
import com.google.gson.JsonElement;


public class TTrackerScraper {



    public void scrapeBasicDetails(Streamer streamer) {
        EntityManager em = JPAUtil.getEntityManager();
        em.getTransaction().begin();

        String url = "https://twitchtracker.com/api/channels/summary/" + streamer.getNameUrl();

        System.out.println("url: " + url);

        HttpClient client = HttpClient.newHttpClient();

        // Build a request.
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();

            // Parse the JSON data using Gson's JsonParser
            JsonElement jsonElement = JsonParser.parseString(responseBody);
            JsonObject jsonObject = jsonElement.getAsJsonObject();

            // Extract each element from the JSON object
            int rank = jsonObject.get("rank").getAsInt();
            int minutesStreamed = jsonObject.get("minutes_streamed").getAsInt();
            int avgViewers = jsonObject.get("avg_viewers").getAsInt();
            int maxViewers = jsonObject.get("max_viewers").getAsInt();
            int hoursWatched = jsonObject.get("hours_watched").getAsInt();
            int followers = jsonObject.get("followers").getAsInt();
            int followersTotal = jsonObject.get("followers_total").getAsInt();

            streamer.setFollowers(followersTotal);
            streamer.setMinutesStreamed(minutesStreamed);
            streamer.setAverageViewers(avgViewers);
            streamer.setPeakViewers(maxViewers);
            streamer.setHoursWatched(hoursWatched);

            if (!em.contains(streamer)) {
                streamer = em.merge(streamer); // Use merge instead of persist
            } else {
                em.persist(streamer);
            }
            em.getTransaction().commit();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            em.close();
        }
    }








    public static void updateAllStreamers(){
        EntityManager em = JPAUtil.getEntityManager();  
        em.getTransaction().begin();
        
        TypedQuery<Streamer> query = em.createQuery("SELECT s FROM Streamer s", Streamer.class);
        List<Streamer> retStreamers = query.getResultList();

        for(Streamer streamer: retStreamers){
            //scrapeBasicDetails(streamer);
        }
        em.close();
    }




    
}
