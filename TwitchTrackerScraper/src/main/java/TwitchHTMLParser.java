import Domain.Streamer;
import Utils.JPAUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;


public class TwitchHTMLParser {

    /**
     * Extracts the titles from the provided HTML content.
     * The method uses Jsoup to parse the HTML and select specific elements with titles.
     *
     * @param html the HTML content as a string from which titles are to be extracted.
     * @return a list of title strings extracted from the HTML content.
     */
    public static List<String> extractTitles(String html) {
        List<String> titles = new ArrayList<>();
        Document doc = Jsoup.parse(html);
        
        // This selector finds h3 tags with a class "CoreText-sc-1txzju1-0 MveHm" which also have a title attribute
        Elements titleElements = doc.select("h3.CoreText-sc-1txzju1-0.MveHm[title]");
        
        // This selector finds p tags with a specific data-a-target attribute
        Elements channelElements = doc.select("p[data-a-target='preview-card-channel-link'][title]");

        // Add all h3 titles to the list
        for (Element titleElement : titleElements) {
            titles.add(titleElement.attr("title"));
        }
        
        // Add all channel titles to the list
        for (Element channelElement : channelElements) {
            titles.add(channelElement.attr("title"));
        }

        return titles;
    }


    /**
     * Extracts the href attributes from anchor tags in the provided HTML content.
     * The method uses Jsoup to parse the HTML and select anchor elements with a specific data attribute.
     *
     * @param html the HTML content as a string from which hrefs are to be extracted.
     * @return a list of href strings extracted from the HTML content.
     */
    public static List<String> extractHrefs(String html) {
        List<String> hrefs = new ArrayList<>();
        Document doc = Jsoup.parse(html);
        // Selecting all <a> tags with a "data-a-target" attribute equal to "preview-card-channel-link"
        Elements links = doc.select("a[data-a-target='preview-card-channel-link']");

        for (Element link : links) {
            String href = link.attr("href");
            hrefs.add(href);
        }

        return hrefs;
    }

    /**
     * Extracts Twitch usernames from the href attributes of anchor tags in the provided HTML content.
     * The method parses the HTML to find specific anchor elements and extracts the portion of the href
     * that corresponds to the username.
     *
     * @param html the HTML content as a string from which usernames are to be extracted.
     * @return a list of usernames extracted from the HTML content.
     */
    public static List<String> extractUsernames(String html) {
        List<String> hrefs = new ArrayList<>();
        Document doc = Jsoup.parse(html);
        // Selecting all <a> tags with a "data-a-target" attribute equal to "preview-card-channel-link"
        Elements links = doc.select("a[data-a-target='preview-card-channel-link']");
        String base = "www.twitch.tv/";

        for (Element link : links) {
            String href = link.attr("href");
            String extracted = href.substring(href.indexOf(base) + base.length());
            hrefs.add(extracted);
        }

        return hrefs;
    }




    /**
     * Loads HTML content from a file into a string.
     *
     * @param filePath the path to the file containing the HTML content.
     * @return a string containing the content of the HTML file.
     * @throws IOException if an I/O error occurs reading from the file or a malformed or unmappable byte sequence is read.
     */
    public static String loadHTMLContent(String filePath) throws IOException {
        // Reads the content of the file to a String
        return new String(Files.readAllBytes(Paths.get(filePath)));
    }


    /**
     * Retrieves a list of Twitch usernames from an HTML file.
     * This method combines the functionality of loading HTML content and extracting usernames.
     *
     * @param filePath the path to the file containing the HTML content.
     * @return a list of usernames or null if an exception occurs.
     */
    public static List<String> getUsernames(String filePath) {
        try {
            String htmlContent = loadHTMLContent(filePath);
            List<String> titles = extractTitles(htmlContent);
            List<String> hrefs = extractUsernames(htmlContent);

            return hrefs;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Creates Domain.Streamer entities from a list of usernames and persists them in the database.
     * The method uses JPA EntityManager to begin a transaction, persist the streamer entities, and commit the transaction.
     *
     * @param streamerNames a list of Twitch streamer usernames to be persisted as Domain.Streamer entities.
     */
    public static void createStreamerFromNames(List<String> streamerNames ) {
        EntityManager em = JPAUtil.getEntityManager();
        em.getTransaction().begin();

        for (String streamerName : streamerNames) {
            Streamer streamer = new Streamer(streamerName);
            em.persist(streamer);
        }

        em.getTransaction().commit();

        TypedQuery<Streamer> query = em.createQuery("SELECT s FROM Domain.Streamer s", Streamer.class);
        List<Streamer> reteNames = query.getResultList();

        System.out.println("restSize: "+ reteNames.size());

        em.close();
    }
}