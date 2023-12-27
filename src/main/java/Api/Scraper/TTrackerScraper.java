package Api.Scraper;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import Api.Domain.Streamer;
import Api.Domain.Streams;
import Api.Domain.StreamsRepository;
import Api.SpringUtils.StreamerService;
import Api.Domain.StreamerRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.UpdateOptions;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.gson.JsonElement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class provides methods for scraping data from TwitchTracker.com and storing it in a database.
 * It includes methods for scraping basic details of a streamer, scraping all streams of a streamer, and scraping all streamers in a list.
 * The class uses Jsoup for web scraping and JPA for database operations.
 */
@Service
public class TTrackerScraper {


    @Autowired
    private StreamerRepository streamerRepository;

    @Autowired
    private StreamsRepository streamsRepository;

    @Autowired
    private StreamerService streamerService;


    private AtomicInteger curScrape = new AtomicInteger(0);
    private AtomicInteger totalScrape = new AtomicInteger(0);

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

                    //Optional<Streamer> streamerOptional = streamerRepository.findByNameUrl(streamer.getNameUrl());

                    // if (streamerOptional.isPresent()) {
                    //     streamer = streamerOptional.get();
                    //     // Update the streamer object with scraped data
                    // } else {
                    //     streamer = new Streamer(streamer.getNameUrl());
                    //     // Set scraped data to the new streamer object
                    // }
                    streamerRepository.save(streamer);
                        

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


    public int getCurScrape(){
        return this.curScrape.get();
    }

    public int getTotalScrape(){
        return this.totalScrape.get();
    }




    /**
     * Scrapes all streams from TwitchTracker for the specified streamer.
     * The method retries scraping a predefined number of times with delays if it fails.
     *
     * @param streamer the streamer whose streams are to be scraped.
     */
    public void scrapeAllStreams(Streamer streamer) {
        final int MAX_STREAMS = 50; // Maximum number of streams to scrape
        int streamCount = 0;
    
        int maxRetries = 3; // Maximum number of retries
        int retryDelay = 5000; // Delay in milliseconds (5000 ms = 5 seconds)
        int retryCount = 0; // Current retry attempt
    
        boolean success = false; // Flag to indicate if the scraping was successful

        List<Streams> streamsBatch = new ArrayList<>();

        List<Streams> recentStreams = streamsRepository.findTop100ByStreamerOrderByDateDesc(streamer);


    
        while (retryCount < maxRetries && !success) {
            try {
    
                String url = "https://twitchtracker.com/" + streamer.getNameUrl() + "/streams";
                Document doc = Jsoup.connect(url).get();
    
                // Select the table with id 'streams'
                Element table = doc.select("table#streams").first();
                
                // Select all 'tr' elements from the table body 'tbody'
                Elements rows = table.select("tbody tr");
    
                //Scroll through each stream
                for (Element row : rows) {
                    // Extract data for each column
                    if (streamCount >= MAX_STREAMS) {
                        break; // Exit the loop if the maximum number of streams is reached
                    }
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

                     // Check if the stream already exists in the recentStreams list
                    boolean streamExists = recentStreams.stream().anyMatch(existing -> existing.getDate().equals(date) && existing.getTitle().equals(title));

                    if (!streamExists) {
                        stream.setStreamer(streamer);
                        streamer.addStream(stream);
                        streamsRepository.save(stream);
                        // streamsBatch.add(stream);
                        streamerRepository.save(streamer);
                    }else{
                        System.out.println("Stream already exists");
                    }
                    
                    streamCount++;
                    
                }
    

                
                
                success = true; // Scrape was successful, no need to retry
            } catch (Exception e) {
                // Log the exception and retry
                System.out.println("Scrape attempt failed, retrying... Attempt: " + (retryCount + 1));
                e.printStackTrace();
                retryCount++; // Increment retry attempt counter
    

                if (retryCount < maxRetries) {
                    try {
                        Thread.sleep(retryDelay); // Delay before the next retry
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt(); // Restore the interrupted status
                    }
                }
            }
        }

        System.out.println("streamsBatch size: " + streamsBatch.size());
        // if (!streamsBatch.isEmpty()) {
        //     streamsRepository.saveAll(streamsBatch); // Save all streams at once
        // }
    
        if (!success) {
            System.out.println("Scraping failed after " + maxRetries + " attempts.");
        }
    }

    @Async
    @Transactional
    public void scrapeStreamerList(List<String> streamerList){
        Streamer streamer;
        this.curScrape.set(0);
        this.totalScrape.set(streamerList.size());
        for (String streamerUrl : streamerList) {
            streamerService.addStreamer(streamerUrl);
            streamer = streamerService.getStreamer(streamerUrl);
            scrapeBasicDetails(streamer);
            scrapeAllStreams(streamer);
            this.curScrape.incrementAndGet();
        }
    }
    


    /** 
     * Scrapes TwitchTracker for all streamers' basic details and their streams.
     * This method is responsible for managing its own EntityManager.
     */
    // public static void scrapeWholeHTML(){
    //     EntityManager em = JPAUtil.getEntityManager();  
    //     try {
    //         em.getTransaction().begin();
    
    //         List<String> streamerNames = TwitchHTMLParser.getUsernames("TwitchHTML//twitch.html");
    //         System.out.println("scrapeWholeDoc retrieved names: " + streamerNames.size());
    //         createOrUpdateStreamers(streamerNames, em);  // Modified method for creating/updating streamers
    
    //         em.getTransaction().commit();
    //     } catch (Exception e) {
    //         e.printStackTrace();
    //         if (em.getTransaction().isActive()) {
    //             em.getTransaction().rollback();
    //         }
    //     } finally {
    //         em.close();
    //     }
    
    //     // Scraper.TTrackerScraper scraper = new Scraper.TTrackerScraper();
    //     // scrapeAllStreamers(scraper);  // This method needs to handle its own EntityManager
    // }


    public void createOrUpdateStreamers(List<String> streamerNames) {
        for (String nameUrl : streamerNames) {
            Optional<Streamer> existingStreamer = streamerRepository.findByNameUrl(nameUrl);

            if (!existingStreamer.isPresent()) {
                // If the streamer doesn't exist, create a new one
                Streamer newStreamer = new Streamer();
                newStreamer.setNameUrl(nameUrl);
                // Set other properties of newStreamer as needed
                streamerRepository.save(newStreamer);
                System.out.println("Created streamer " + newStreamer.getNameUrl());
            } else {
                // If a Streamer with this nameUrl already exists, you can update it
                Streamer streamerToUpdate = existingStreamer.get();
                // Update properties of streamerToUpdate as needed
                streamerRepository.save(streamerToUpdate);
                System.out.println(streamerToUpdate.getNameUrl() + " already exists, updated details");
            }
        }
    }

    public void scrapeSingleStreamer(String streamerUrl){
        TTrackerScraper scraper = new TTrackerScraper();

        Optional<Streamer> optionalStreamer = streamerRepository.findByNameUrl(streamerUrl);
        Streamer streamer = optionalStreamer.orElse(new Streamer()); // Default Streamer object
        
        scraper.scrapeBasicDetails(streamer);
        scraper.scrapeAllStreams(streamer);
    }
    


    /**
     * Initiates scraping of basic details and all streams for a list of streamers.
     *
     * @param scraper the Scraper.TTrackerScraper instance used for scraping operations.
     */
    public void scrapeAllStreamers(){
        List<Streamer> streamers = streamerRepository.findAll();
        for (Streamer streamer : streamers) {
            scrapeBasicDetails(streamer);
            scrapeAllStreams(streamer);
        }
    }


    




    //depreciated
    // public static void updateAllStreamers(){
    //     EntityManager em = JPAUtil.getEntityManager();  
    //     em.getTransaction().begin();
        
    //     TypedQuery<Streamer> query = em.createQuery("SELECT s FROM Domain.Streamer s", Streamer.class);
    //     List<Streamer> retStreamers = query.getResultList();

    //     for(Streamer streamer: retStreamers){
    //         ///scrapeBasicDetails(streamer);
    //     }
    //     em.close();
    // }






    
}
