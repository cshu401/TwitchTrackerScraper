import java.util.List;

import javax.persistence.*;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;




// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    public static void main(String[] args) {
        TwitchUsernameScraper scraper = new TwitchUsernameScraper("vtuber");
        scraper.scrape();
    }
}