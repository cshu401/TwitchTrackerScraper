package Api;

import Api.Domain.Streamer;
import Api.Domain.StreamsRepository;
import Api.SpringUtils.StreamerService;
import Api.Scraper.TTrackerScraper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api")
public class ApiController {

    
    @Autowired
    private StreamerService streamerService;

    @Autowired
    private StreamsRepository streamsRepository;

    @Autowired
    private TTrackerScraper tScraper;

    private static final Logger log = LoggerFactory.getLogger(ApiController.class);


    public ApiController(){
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

    @GetMapping("/getAll")
    public ResponseEntity<String> getAllStreamers(){
        return ResponseEntity.ok(streamerService.getAllStreamers().toString());
    }
    



    // Add more endpoints and methods here

}
