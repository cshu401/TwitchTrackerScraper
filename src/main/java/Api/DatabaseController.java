/**
 * This class represents a controller for managing the database operations related to streamers.
 * It provides endpoints for adding, scraping, updating, and deleting streamers.
 */
package Api;

import Api.Domain.Streamer;
import Api.SpringUtils.StreamerService;
import Api.Scraper.TTrackerScraper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/database")
public class DatabaseController {
    
        
        @Autowired
        private StreamerService streamerService;
    
        @Autowired
        private TTrackerScraper tScraper;
    
        private static final Logger log = LoggerFactory.getLogger(DatabaseController.class);

/**
     * Endpoint for adding a streamer to the database.
     * @param streamerUrl The URL of the streamer to be added.
     * @return ResponseEntity indicating the success or failure of the operation.
     */
        @PostMapping("/add")
        public ResponseEntity<String> addStreamer(@RequestParam String streamerUrl) {
            boolean isAdded = streamerService.addStreamer(streamerUrl);
            if (isAdded) {
                return ResponseEntity.ok("Streamer added successfully.");
            } else {
                return ResponseEntity.badRequest().body("Failed to add streamer.");
            }
        }

    /**
     * Endpoint for adding a streamer to the database and scraping their details.
     * @param streamerUrl The URL of the streamer to be added and scraped.
     * @return ResponseEntity indicating the success or failure of the operation.
     */
        @PostMapping("/addScrape")
        public ResponseEntity<String> addStreamerAndScrape(@RequestParam String streamerUrl) {
            try{
            boolean isAdded = streamerService.addStreamer(streamerUrl);
            Streamer streamer = streamerService.getStreamer(streamerUrl);
            
            tScraper.scrapeBasicDetails(streamer);
            tScraper.scrapeAllStreams(streamer);
            return ResponseEntity.ok("Streamer added and scraped.");
            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.badRequest().body("Failed to add streamer.");
            }
        }

/**
     * Endpoint for scraping the details of a streamer.
     * @param streamerUrl The URL of the streamer to be scraped.
     * @return ResponseEntity indicating the success or failure of the operation.
     */
        @PostMapping("/Scrape")
        public ResponseEntity<String> scrapeStreamer(@RequestParam String streamerUrl) {
            Streamer streamer = streamerService.getStreamer(streamerUrl);
            tScraper.scrapeBasicDetails(streamer);
            tScraper.scrapeAllStreams(streamer);
            return ResponseEntity.ok("Streamer scraped successfully.");
        }

        /**
     * Endpoint for scraping the details of a list of streamers.
     * @param streamerList The list of streamer URLs to be scraped.
     * @return ResponseEntity indicating the success or failure of the operation.
     */
    @PostMapping("/scrapeList")
    ResponseEntity<String> scrapeList(@RequestBody List<String> streamerList){
            try{
                tScraper.scrapeStreamerList(streamerList);
                return ResponseEntity.ok("Scrape started, use endpoint scrapeStatus to check status...");
            } catch (Exception e) {
                return ResponseEntity.badRequest().body("Failed to scrape streamer list.");
            }
        }

        /**
     * Endpoint for retrieving the current scraping status.
     * @return ResponseEntity containing the current scraping progress.
     */
        @GetMapping("/scrapeStatus")
        public ResponseEntity<String> getScrapeStatus() {
            int current = tScraper.getCurScrape();
            int total = tScraper.getTotalScrape();
            String status = "Current scraping progress: " + current + " out of " + total;
            return ResponseEntity.ok(status);
        }
        
    /**
     * Endpoint for updating streamers based on a threshold value.
     * @param threshold The threshold value for updating streamers.
     * @return ResponseEntity indicating the success or failure of the operation.
     */
        @PostMapping("/updateStreamers")
        public ResponseEntity<String> updateStreamers(@RequestParam int threshold) {
            try {
                List<String> streamers = streamerService.findStreamersToUpdate(threshold);
                tScraper.scrapeStreamerList(streamers);
                return ResponseEntity.ok("Good! scraping " + streamers.size() + " amount of streamers");
            } 
            catch (Exception e) {
                return ResponseEntity.badRequest().body("Failed to scrape streamer list.");
            }
        }

    /**
     * Endpoint for deleting a streamer from the database.
     * @param streamerUrl The URL of the streamer to be deleted.
     * @return ResponseEntity indicating the success or failure of the operation.
     */
        @DeleteMapping("/delete")
        public ResponseEntity<String> deleteStreamer(@RequestParam String streamerUrl) {
            boolean isDeleted = streamerService.deleteStreamer(streamerUrl);
            if (isDeleted) {
                return ResponseEntity.ok("Streamer deleted successfully.");
            } else {
                return ResponseEntity.badRequest().body("Failed to delete streamer.");
            }
        }
    
}
