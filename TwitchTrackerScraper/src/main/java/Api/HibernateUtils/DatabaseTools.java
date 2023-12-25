package Api.HibernateUtils;


import Api.Domain.Streamer;
import Api.Domain.StreamerRepository;
import Api.Scraper.TTrackerScraper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;



@Service
public class DatabaseTools {

    @Autowired
    private StreamerRepository streamerRepository;

    public boolean deleteStreamer(String streamerUrl) {
        try {
            Optional<Streamer> optionalStreamer = streamerRepository.findByNameUrl(streamerUrl);
            Streamer streamer = optionalStreamer.orElse(new Streamer()); // Default Streamer object
            if (streamer != null) {
                streamerRepository.delete(streamer);
                System.out.println("Streamer deleted successfully.");
                return true;
            } else {
                System.out.println("Streamer not found with URL: " + streamerUrl);
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    
    public boolean addStreamer(String streamerUrl) {
        if (streamerUrl == null || streamerUrl.isEmpty()) {
            System.out.println("Streamer URL cannot be null or empty.");
            return false;
        }

        try {
            // Check if the streamer already exists
            Optional<Streamer> existingStreamer = streamerRepository.findByNameUrl(streamerUrl);
            Streamer streamer = existingStreamer.orElseGet(() -> {
                Streamer newStreamer = new Streamer();
                newStreamer.setNameUrl(streamerUrl);
                // Set other properties of newStreamer as needed
                return newStreamer;
            });

            // Save the streamer (creates new or updates existing)
            streamerRepository.save(streamer);
            System.out.println("Streamer added or updated successfully.");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    



}
