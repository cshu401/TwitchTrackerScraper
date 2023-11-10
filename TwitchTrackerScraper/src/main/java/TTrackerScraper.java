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
import com.google.gson.JsonSyntaxException;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.io.IOException;
import com.google.gson.JsonElement;


public class TTrackerScraper {


    public void scrapeBasicDetails(Streamer streamer) {
        int retryCount = 0;
        boolean success = false;
        String responseBody = null;
        int maxRetryAttempts = 5;
        long retryDelayMillis = 5000; // 5 seconds delay
        EntityManager em = JPAUtil.getEntityManager();
        em.getTransaction().begin();
    
        while (retryCount < maxRetryAttempts && !success) {
            String url = "https://twitchtracker.com/api/channels/summary/" + streamer.getNameUrl();
            System.out.println("url: " + url);
    
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();
    
            try {
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                responseBody = response.body();
    
                if (response.statusCode() == 200 && responseBody != null && !responseBody.isEmpty()) {
                    // existing JSON parsing and database operations...

                    JsonElement jsonElement = JsonParser.parseString(responseBody);
                    JsonObject jsonObject = jsonElement.getAsJsonObject();

                    int rank = getIntFromJson(jsonObject, "rank", -1);
                    int minutesStreamed = getIntFromJson(jsonObject, "minutes_streamed", -1);
                    int avgViewers = getIntFromJson(jsonObject, "avg_viewers", -1);
                    int maxViewers = getIntFromJson(jsonObject, "max_viewers", -1);
                    int hoursWatched = getIntFromJson(jsonObject, "hours_watched", -1);
                    int followers = getIntFromJson(jsonObject, "followers", -1);
                    int followersTotal = getIntFromJson(jsonObject, "followers_total", -1);

                    streamer.setFollowers(followersTotal);
                    streamer.setMinutesStreamed(minutesStreamed);
                    streamer.setAverageViewers(avgViewers);
                    streamer.setPeakViewers(maxViewers);
                    streamer.setHoursWatched(hoursWatched);
                    streamer.setLastScrapedToNow();

                    if (!em.contains(streamer)) {
                    em.merge(streamer); // Use merge instead of persist
                    } else {
                        em.persist(streamer);
                    }
                    em.getTransaction().commit();
                        

                    success = true;
                } else if (response.statusCode() == 503) {
                    System.out.println("Service unavailable (503). Retrying after delay...");
                    retryCount++;
                    Thread.sleep(retryDelayMillis); // Delay before retrying
                } else {
                    System.out.println("Unexpected response status: " + response.statusCode());
                    retryCount++;
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            } catch (JsonSyntaxException e) {
                System.out.println("Malformed JSON: " + responseBody);
                System.out.println("JSON syntax exception: " + e.getMessage());
                retryCount++;
            } finally {
                
            }
        }

        em.close();
    
        if (!success) {
            System.out.println("Failed to retrieve data after " + maxRetryAttempts + " retries.");
        }
    }

    private int getIntFromJson(JsonObject jsonObject, String fieldName, int defaultValue) {
        JsonElement element = jsonObject.get(fieldName);
        if (element != null && !element.isJsonNull()) {
            return element.getAsInt();
        }
        return defaultValue;
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
