import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class TwitchUsernameScraper {


        private String URL;
        private WebDriver driver;

        public TwitchUsernameScraper(String tag) {
            this.URL = "https://www.twitch.tv/directory/all/tags/" + tag;
            this.driver = new ChromeDriver();
        }

            public void importCookies(String cookieFilePath) {
            try {
                // Navigate to the site
                driver.get(URL);

                // Read the cookies from a file
                File file = new File(cookieFilePath);
                FileReader fileReader = new FileReader(file);
                BufferedReader bufferedReader = new BufferedReader(fileReader);
                String strLine;
                while ((strLine = bufferedReader.readLine()) != null) {
                    StringTokenizer token = new StringTokenizer(strLine, ";");
                    while (token.hasMoreTokens()) {
                        String name = token.nextToken();
                        String value = token.nextToken();
                        String domain = token.nextToken();
                        // ... other cookie attributes
                        //Cookie ck = new Cookie(name, value, domain, "/", null);
                        //driver.manage().addCookie(ck);  // This will add the stored cookie to your current session
                    }
                }
                bufferedReader.close();
                fileReader.close();

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        // Method to initiate the scraping process
        public void scrape() {
            driver.get(URL);
            String htmlContent = driver.getPageSource();
            System.out.println(htmlContent);
        }
    
        // Method to parse the HTML document
        private void parseDocument(Document document) {
            // Your parsing logic here.
            // Use Jsoup selectors to extract the data you need.
            
            Elements vtubers = document.select(".some-css-class");
            String html = "<p data-a-target=\"preview-card-channel-link\" tabindex=\"-1\" title=\"宙星ぱる (soraposhich)\" class=\"CoreText-sc-1txzju1-0 gBknDX\">宙星ぱる (soraposhich)</p>";
        
            // Parse the HTML string with jsoup
            Document doc = Jsoup.parse(html);
            
            // Use a CSS selector to find the element with the specific class
            Element pElement = doc.select("p.CoreText-sc-1txzju1-0").first();
            
            // Extract the title attribute value
            String title = pElement.attr("title");
            
            // Print the extracted title
            System.out.println("Title extracted: " + title);
        }
    
}
