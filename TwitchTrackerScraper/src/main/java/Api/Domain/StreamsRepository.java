package Api.Domain;

import org.springframework.data.mongodb.repository.MongoRepository;
import Api.Domain.Streams;
import java.util.List;

public interface StreamsRepository extends MongoRepository<Streams, Long> {
    // Define custom query methods here


    List<Streams> findByStreamer(Streamer streamer);

    List<Streams> findTop100ByStreamerOrderByDateDesc(Streamer streamer);


}
