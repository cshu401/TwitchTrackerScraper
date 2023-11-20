package Api.SpringUtils;

import org.springframework.data.jpa.repository.JpaRepository;
import Api.Domain.Streamer;
import java.util.Optional;

public interface StreamerRepository extends JpaRepository<Streamer, Long> {
    // Define custom query methods here

    // Example: Method to find a Streamer by its URL
    Optional<Streamer> findByNameUrl(String nameUrl);

    
}
