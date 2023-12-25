package Api.Domain;

import org.springframework.data.mongodb.repository.MongoRepository;
import Api.Domain.Streamer;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface StreamerRepository extends MongoRepository<Streamer, Long> {
    // Define custom query methods here

    // Example: Method to find a Streamer by its URL
    Optional<Streamer> findByNameUrl(String nameUrl);

    List<Streamer> findByLastScrapedBefore(LocalDateTime threshold);

    
}
