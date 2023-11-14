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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.IOException;
import com.google.gson.JsonElement;


/**
 * This class provides methods for scraping data from TwitchTracker.com and storing it in a database.
 * It includes methods for scraping basic details of a streamer, scraping all streams of a streamer, and scraping all streamers in a list.
 * The class uses Jsoup for web scraping and JPA for database operations.
 */
public class TTrackerScraper {


    /**
     * Scrapes basic details of a given streamer and stores them in the database.
     * The scraping is attempted for a maximum number of retries defined by maxRetryAttempts.
     * If a retry is needed, a delay is introduced between retries.
     *
     * @param streamer the streamer object containing the nameUrl used to scrape data.
     */
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







    /**
     * Scrapes all streams from TwitchTracker for the specified streamer.
     * The method retries scraping a predefined number of times with delays if it fails.
     *
     * @param streamer the streamer whose streams are to be scraped.
     */
    public void scrapeAllStreams(Streamer streamer) {
        EntityManager em = JPAUtil.getEntityManager();
    
        int maxRetries = 3; // Maximum number of retries
        int retryDelay = 5000; // Delay in milliseconds (5000 ms = 5 seconds)
        int retryCount = 0; // Current retry attempt
    
        boolean success = false; // Flag to indicate if the scraping was successful
    
        while (retryCount < maxRetries && !success) {
            try {
                em.getTransaction().begin();
    
                String url = "https://twitchtracker.com/" + streamer.getNameUrl() + "/streams";
                Document doc = Jsoup.connect(url).get();
    
                // Select the table with id 'streams'
                Element table = doc.select("table#streams").first();
                
                // Select all 'tr' elements from the table body 'tbody'
                Elements rows = table.select("tbody tr");
    
                for (Element row : rows) {
                    // Extract data for each column
                    String dateStr = row.select("td").get(0).text();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                    LocalDateTime date = LocalDateTime.parse(dateStr, formatter);
                    String duration = row.select("td").get(1).text();
                    int durationMin = Integer.parseInt(duration);
                    int avgCCV = Integer.parseInt(row.select("td").get(2).text());
                    int maxCCV = Integer.parseInt(row.select("td").get(3).text());
                    int followers = Integer.parseInt(row.select("td").get(4).text());
                    String views = row.select("td").get(5).text();
                    String title = row.select("td").get(6).text();
                    Streams stream = new Streams(title, date, avgCCV, maxCCV, followers, durationMin);
                    stream.setStreamer(streamer);
                    streamer.addStream(stream);
    
                    em.persist(stream);
                }
    
                em.merge(streamer);
                em.getTransaction().commit();

                success = true; // Scrape was successful, no need to retry
            } catch (Exception e) {
                // Log the exception and retry
                System.out.println("Scrape attempt failed, retrying... Attempt: " + (retryCount + 1));
                e.printStackTrace();
                retryCount++; // Increment retry attempt counter
    
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback(); // Roll back if the transaction is still active
                }
    
                if (retryCount < maxRetries) {
                    try {
                        Thread.sleep(retryDelay); // Delay before the next retry
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt(); // Restore the interrupted status
                    }
                }
            }
        }
    
        if (!success) {
            System.out.println("Scraping failed after " + maxRetries + " attempts.");
        }

         em.close();
    }
    


    /**
     * Scrapes TwitchTracker for all streamers' basic details and their streams.
     * This method is responsible for managing its own EntityManager.
     */
    public static void scrapeWholeHTML(){
        EntityManager em = JPAUtil.getEntityManager();  
        try {
            em.getTransaction().begin();
    
            List<String> streamerNames = TwitchHTMLParser.getUsernames("TwitchHTML//twitch.html");
            System.out.println("scrapeWholeDoc retrieved names: " + streamerNames.size());
            createOrUpdateStreamers(streamerNames, em);  // Modified method for creating/updating streamers
    
            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
        } finally {
            em.close();
        }
    
        // TTrackerScraper scraper = new TTrackerScraper();
        // scrapeAllStreamers(scraper);  // This method needs to handle its own EntityManager
    }


    /**
     * Creates or updates a list of streamers in the database.
     * New streamers are persisted, and existing streamers can be updated as needed.
     *
     * @param streamerNames a list of streamer names to be created or updated.
     * @param em the EntityManager used to perform JPA operations.
     */
    public static void createOrUpdateStreamers(List<String> streamerNames, EntityManager em) {
        for (String nameUrl : streamerNames) {
            String jpql = "SELECT s FROM Streamer s WHERE s.nameUrl = :nameUrl";
            List<Streamer> existingStreamers = em.createQuery(jpql, Streamer.class)
                                                 .setParameter("nameUrl", nameUrl)
                                                 .getResultList();
    
            if (existingStreamers.isEmpty()) {
                Streamer newStreamer = new Streamer();
                newStreamer.setNameUrl(nameUrl);
                // Set other properties of newStreamer as needed
                System.out.println("Created streamer " + newStreamer.getNameUrl());
                em.persist(newStreamer);
            } else {
                // If a Streamer with this nameUrl already exists, you can update it
                Streamer existingStreamer = existingStreamers.get(0);
                System.out.println(existingStreamer.getNameUrl() + " already exists");
                // Update properties of existingStreamer as needed
            }
        }
    }

    public static void scrapeSingleStreamer(String streamerUrl){
        EntityManager em = JPAUtil.getEntityManager();  
        em.getTransaction().begin();
        TTrackerScraper scraper = new TTrackerScraper();

        Streamer streamer = StreamerTools.getStreamerByNameUrl(streamerUrl);
        
        
        scraper.scrapeBasicDetails(streamer);
        scraper.scrapeAllStreams(streamer);
        em.getTransaction().commit();
        em.close();
    }
    


    /**
     * Initiates scraping of basic details and all streams for a list of streamers.
     *
     * @param scraper the TTrackerScraper instance used for scraping operations.
     */
    public static void scrapeAllStreamers(TTrackerScraper scraper ){
        List<Streamer> streamers = StreamerTools.getAllStreamers();
        int count = 0;
        int total = streamers.size();
        for (Streamer streamer : streamers) {
            scraper.scrapeBasicDetails(streamer);
            scraper.scrapeAllStreams(streamer);
            UserInterface.printLoadingBar(count,total, 50);
            count++;
        }
    }


    




    //depreciated
    public static void updateAllStreamers(){
        EntityManager em = JPAUtil.getEntityManager();  
        em.getTransaction().begin();
        
        TypedQuery<Streamer> query = em.createQuery("SELECT s FROM Streamer s", Streamer.class);
        List<Streamer> retStreamers = query.getResultList();

        for(Streamer streamer: retStreamers){
            ///scrapeBasicDetails(streamer);
        }
        em.close();
    }






    
}
