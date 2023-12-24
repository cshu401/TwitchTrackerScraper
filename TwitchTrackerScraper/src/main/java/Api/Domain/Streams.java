package Api.Domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import org.springframework.data.mongodb.core.mapping.DBRef;
import java.time.LocalDateTime;

@Document
public class Streams {

    @Id
    private Long id;

    @DBRef
    private Streamer streamer;

    // Other fields that might be relevant for a Stream, like title, date, etc.
    // For example:
    private String title;

    //@Column(unique = true)
    private LocalDateTime date;

    private int averageViews = 0;

    private int maxViews = 0;

    private int followers = 0;

    private int durationMinutes = 0;

    // Default constructor
    public Streams() {
    }

    // Constructor with parameters
    public Streams(String title, LocalDateTime date, int averageViews, int maxViews, int followers, int durationMinutes) {
        this.title = title;
        this.date = date;
        this.averageViews = averageViews;
        this.maxViews = maxViews;
        this.followers = followers;
        this.durationMinutes = durationMinutes;
        this.streamer = streamer;
    }

    public int getAverageViews() {
        return this.averageViews;
    }

    public void setAverageViewers(int averageViews) {
        this.averageViews = averageViews;
    }

    public int getMaxViews() {
        return this.maxViews;
    }

    public void setMaxViews(int maxViews) {
        this.maxViews = maxViews;
    }

    public int getFollowers(){
        return this.followers;
    }

    public void getFollowers(int followers){
        this.followers = followers;
    }

    public void setDurationMinutes(int durationMinutes){
        this.durationMinutes = durationMinutes;
    }

    public int getDurationMinutes(){
        return this.durationMinutes;
    }

    // Getter and setter for id
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    @Override
    public String toString() {
        return "Stream{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", date=" + date +
                ", averageViews=" + averageViews +
                ", maxViews=" + maxViews +
                ", followers=" + followers +
                ", durationMinutes=" + durationMinutes +
                ", streamerId=" + (streamer != null ? streamer.getId() : "null") +
                '}';
    }

}
