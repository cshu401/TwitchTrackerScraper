import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.persistence.*;

@Entity
public class Streamer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String nameUrl;

    @OneToMany(mappedBy = "streamer", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Streams> streams = new ArrayList<>();

    private LocalDateTime lastScraped;

    //Information from Twitch
    private double minutesStreamed = 0;
    private double averageViewers = 0;
    private double peakViewers = 0;
    private double hoursWatched = 0;
    private double followersPerHour = 0;
    private double followers = 0;


    // Default constructor
    public Streamer() {}

    // Constructor with nameUrl parameter
    public Streamer(String nameUrl) {
        if(nameUrl == null || nameUrl.isEmpty()) {
            throw new IllegalArgumentException("Streamer nameUrl cannot be null or empty");
        }
        this.nameUrl = nameUrl;
    }


    // Getter and Setter methods
    public LocalDateTime getLastScraped() {
        return lastScraped;
    }

    public void setLastScrapedToNow() {
        this.lastScraped = LocalDateTime.now();
    }

    public double getFollowers(){
        return this.followers;
    }

    public void setFollowers(double followers) {
        this.followers = followers;
    }

    public double getMinutesStreamed() {
        return minutesStreamed ;
    }

    public void setMinutesStreamed(double minutesStreamed ) {
        this.minutesStreamed  = minutesStreamed ;
    }

    public double getAverageViewers() {
        return averageViewers;
    }

    public void setAverageViewers(double averageViewers) {
        this.averageViewers = averageViewers;
    }

    public double getPeakViewers() {
        return peakViewers;
    }

    public void setPeakViewers(double peakViewers) {
        this.peakViewers = peakViewers;
    }

    public double getHoursWatched() {
        return hoursWatched;
    }

    public void setHoursWatched(double hoursWatched) {
        this.hoursWatched = hoursWatched;
    }

    public double getFollowersPerHour() {
        return followersPerHour;
    }

    public void setFollowersPerHour(double followersPerHour) {
        this.followersPerHour = followersPerHour;
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

    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Streamer Details:\n");
        sb.append("ID: ").append(id).append("\n");
        sb.append("Name URL: ").append(nameUrl).append("\n");
        sb.append("Last Scraped: ").append(lastScraped).append("\n");
        sb.append("Minutes Streamed: ").append(minutesStreamed).append("\n");
        sb.append("Average Viewers: ").append(averageViewers).append("\n");
        sb.append("Peak Viewers: ").append(peakViewers).append("\n");
        sb.append("Hours Watched: ").append(hoursWatched).append("\n");
        sb.append("Followers Per Hour: ").append(followersPerHour).append("\n");
        sb.append("Followers: ").append(followers).append("\n");
        sb.append("Streams:\n");

        for (Streams stream : streams) {
            sb.append(" - ").append(stream.toString()).append("\n");
            // Assuming Streams class has an overridden toString() method
        }

        return sb.toString();
    }

}
