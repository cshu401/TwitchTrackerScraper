package Api;

import Api.Domain.Streamer;
import Api.SpringUtils.StreamerService;
import Api.HibernateUtils.StreamerTools;
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

        @PostMapping("/add")
        public ResponseEntity<String> addStreamer(@RequestParam String streamerUrl) {
            boolean isAdded = streamerService.addStreamer(streamerUrl);
            if (isAdded) {
                return ResponseEntity.ok("Streamer added successfully.");
            } else {
                return ResponseEntity.badRequest().body("Failed to add streamer.");
            }
        }

        @PostMapping("/addListStreamers")
        public ResponseEntity<String> addListStreamers(@RequestParam String streamerUrl){
            StreamerTools streamerTools = new StreamerTools();
            return null;
        }
    
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

        @PostMapping("/Scrape")
        public ResponseEntity<String> scrapeStreamer(@RequestParam String streamerUrl) {
            Streamer streamer = streamerService.getStreamer(streamerUrl);
            tScraper.scrapeBasicDetails(streamer);
            tScraper.scrapeAllStreams(streamer);
            return ResponseEntity.ok("Streamer scraped successfully.");
        }

        @PostMapping("/scrapeList") ResponseEntity<String> scrapeList(@RequestBody List<String> streamerList){
            try{
                tScraper.scrapeStreamerList(streamerList);
                return ResponseEntity.ok("Scrape started, use endpoint scrapeStatus to check status...");
            } catch (Exception e) {
                return ResponseEntity.badRequest().body("Failed to scrape streamer list.");
            }
        }

        
        @GetMapping("/scrapeStatus")
        public ResponseEntity<String> getScrapeStatus() {
            int current = tScraper.getCurScrape();
            int total = tScraper.getTotalScrape();
            String status = "Current scraping progress: " + current + " out of " + total;
            return ResponseEntity.ok(status);
        }


        

    
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
