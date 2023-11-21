package Api.Domain;

import org.springframework.data.jpa.repository.JpaRepository;
import Api.Domain.Streams;
import java.util.Optional;

public interface StreamsRepository extends JpaRepository<Streams, Long> {
    // Define custom query methods here



}
