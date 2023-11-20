package Api.SpringUtils;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import Api.Domain.Streamer;

import java.util.Optional;

@Service
public class StreamerService {

    @Autowired
    private StreamerRepository streamerRepository;

    public Streamer getStreamer(String streamerUrl){
        Optional<Streamer> streamerOptional = streamerRepository.findByNameUrl(streamerUrl);
        if (streamerOptional.isPresent()) {
            return streamerOptional.get();
        } else {
            System.out.println("Streamer not found with URL: " + streamerUrl);
            return null;
        }
    }

    public boolean addStreamer(String streamerUrl) {
        if (streamerUrl == null || streamerUrl.isEmpty()) {
            System.out.println("Streamer URL cannot be null or empty.");
            return false;
        }

        // Check if the streamer already exists
        Optional<Streamer> existingStreamer = streamerRepository.findByNameUrl(streamerUrl);
        if (existingStreamer.isPresent()) {
            System.out.println("Streamer already exists with URL: " + streamerUrl);
            return false;
        }

        try {
            Streamer newStreamer = new Streamer(streamerUrl);
            streamerRepository.save(newStreamer);
            System.out.println("Streamer added successfully.");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    public boolean deleteStreamer(String streamerUrl) {
        try {
            Optional<Streamer> streamerOptional = streamerRepository.findByNameUrl(streamerUrl);
            if (streamerOptional.isPresent()) {
                streamerRepository.delete(streamerOptional.get());
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

    // Other methods...
}
