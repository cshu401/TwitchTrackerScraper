package Api;

import Api.Domain.Streamer;
import Api.SpringUtils.StreamerService;
import Api.HibernateUtils.StreamerTools;
import Api.Scraper.TTrackerScraper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api")
public class ApiController {

    private final StreamerTools streamerTools;
    
    @Autowired
    private StreamerService streamerService;

    @Autowired
    private TTrackerScraper tScraper;

    private static final Logger log = LoggerFactory.getLogger(ApiController.class);


    public ApiController(StreamerTools streamerTools) {
        this.streamerTools = streamerTools;
    }


    @GetMapping("/hello")
    public String hello() {
        return "Hello, World!";
    }

     @GetMapping("/search")
    public ResponseEntity<Streamer> searchStreamer(@RequestParam String streamerName) {
        Streamer streamer = streamerService.getStreamer(streamerName);
        System.out.println("Streamer found: "+ streamer.getNameUrl());
        return ResponseEntity.ok(streamer);
    }

    @PostMapping("/add")
    public ResponseEntity<String> addStreamer(@RequestParam String streamerUrl) {
        boolean isAdded = streamerService.addStreamer(streamerUrl);
        if (isAdded) {
            return ResponseEntity.ok("Streamer added successfully.");
        } else {
            return ResponseEntity.badRequest().body("Failed to add streamer.");
        }
    }

    @PostMapping("/addScrape")
    public ResponseEntity<String> addStreamerAndScrape(@RequestParam String streamerUrl) {
        boolean isAdded = streamerService.addStreamer(streamerUrl);
        Streamer streamer = streamerService.getStreamer(streamerUrl);
        
        tScraper.scrapeBasicDetails(streamer);
        if (isAdded) {
            return ResponseEntity.ok("Streamer added successfully.");
        } else {
            return ResponseEntity.badRequest().body("Failed to add streamer.");
        }
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

    // Add more endpoints and methods here

}
