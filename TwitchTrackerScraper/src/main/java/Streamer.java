import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.persistence.*;

@Entity
public class Streamer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String nameUrl;

    @OneToMany(mappedBy = "streamer", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    private List<Streams> streams = new ArrayList<>();

    // Default constructor
    public Streamer() {}

    // Constructor with nameUrl parameter
    public Streamer(String nameUrl) {
        this.nameUrl = nameUrl;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNameUrl() {
        return nameUrl;
    }

    public void setNameUrl(String nameUrl) {
        this.nameUrl = nameUrl;
    }

    public List<Streams> getStreams() {
        return streams;
    }

    public void setStreams(List<Streams> streams) {
        this.streams = streams;
    }

    // Add a single stream to the streams list and set this streamer as the stream's streamer
    public void addStream(Streams stream) {
        streams.add(stream);
        stream.setStreamer(this);
    }

    // equals and hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Streamer)) return false;
        Streamer streamer = (Streamer) o;
        return Objects.equals(nameUrl, streamer.nameUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nameUrl);
    }

}
