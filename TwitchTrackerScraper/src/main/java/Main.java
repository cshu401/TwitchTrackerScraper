import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.*;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Scanner;




// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {

    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {

        //scrapeWholeDoc();
        TTrackerScraper scraper = new TTrackerScraper();

        while (true) {
            UserInterface.displayMenu();
            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    TTrackerScraper.scrapeWholeHTML();
                    System.out.println("Update complete!!!");
                    break;
                case 2:
                    //extractToExcel();
                case 3:
                    TTrackerScraper.scrapeAllStreamers(scraper);
                case 4:
                    boolean jpqlMenuActive = true;
                    while (jpqlMenuActive) {
                        UserInterface.displayDatabaseMenu();
                        int jpqlChoice = scanner.nextInt();
                        switch (jpqlChoice) {
                            case 1:
                                // Implement search by Name URL
                                String nameInput = UserInterface.promptStringInput("Please enter a Url to search for");
                                Streamer output = StreamerTools.getStreamerByNameUrl(nameInput);
                                UserInterface.displayStreamerData(output);
                                
                                break;
                            case 2:
                                // return all streamers
                                List<Streamer> streamers = StreamerTools.getAllStreamers();
                                System.out.println("Printing all Streamers...");
                                UserInterface.printStreamersPerLine(streamers,6);
                                break;
                            case 3:
                                // Implement search by Followers
                                break;
                            case 4:
                                jpqlMenuActive = false;
                                break;
                            default:
                                System.out.println("Invalid choice. Please try again.");
                        }
                    }
                    break;
                    
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }




    private static void extractToExcel(){

    }



       



    

}

