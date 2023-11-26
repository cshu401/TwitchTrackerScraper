package Api.Domain;

import org.springframework.data.jpa.repository.JpaRepository;
import Api.Domain.Streams;

import java.util.List;
import java.util.Optional;

public interface StreamsRepository extends JpaRepository<Streams, Long> {
    // Define custom query methods here


    List<Streams> findByStreamer(Streamer streamer);

    List<Streams> findTop100ByStreamerOrderByDateDesc(Streamer streamer);


}
