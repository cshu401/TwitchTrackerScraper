import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


public class TwitchHTMLParser {

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

    //Bad code
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





    public static String loadHTMLContent(String filePath) throws IOException {
        // Reads the content of the file to a String
        return new String(Files.readAllBytes(Paths.get(filePath)));
    }

    public static List<String> getTitleFromHtml() {
        String filePath = "TwitchHTML//twitch.htm";
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


}