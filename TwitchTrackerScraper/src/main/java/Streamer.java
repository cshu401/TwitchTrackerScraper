

import javax.persistence.*;

@Entity
public class Streamer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nameUrl;

    public Streamer() {}

    public Streamer(String nameUrl) {
        this.nameUrl = nameUrl;
    }

    public String getName() {
        return nameUrl;
    }
    
}
