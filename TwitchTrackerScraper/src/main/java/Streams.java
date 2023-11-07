import java.time.LocalDateTime;

import javax.persistence.*;

@Entity
public class Streams {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String streamId; // Unique identifier for the stream

    @ManyToOne
    @JoinColumn(name = "streamer_id") // This will create a foreign key column in 'Streams' table.
    private Streamer streamer;

    // Other fields that might be relevant for a Stream, like title, date, etc.
    // For example:
    private String title;
    private LocalDateTime date;

    // Default constructor
    public Streams() {
    }

    // Constructor with parameters
    public Streams(String streamId, String title, LocalDateTime date) {
        this.streamId = streamId;
        this.title = title;
        this.date = date;
    }

    // Getter and setter for id
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    // Getter and setter for streamId
    public String getStreamId() {
        return streamId;
    }

    public void setStreamId(String streamId) {
        this.streamId = streamId;
    }

    // Getter and setter for streamer
    public Streamer getStreamer() {
        return streamer;
    }

    public void setStreamer(Streamer streamer) {
        this.streamer = streamer;
    }

    // Getters and setters for other fields
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

}
