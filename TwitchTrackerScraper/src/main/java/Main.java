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
                    // Add Streamer
                    String input = UserInterface.promptStringInput("Please enter the name of the streamer you wish to add: ");
                    DatabaseTools.addStreamer(input);
                    break;
                case 3:
                    // Delete Streamer
                    String input1 = UserInterface.promptStringInput("Please enter the name of the streamer you wish to delete: ");
                    DatabaseTools.deleteStreamer(input1);
                    break;
                case 4:
                    TTrackerScraper.scrapeAllStreamers(scraper);
                    break;
                case 5:
                    System.out.println("Exporting to Excel...");
                    ExcelExporter.exportToExcel();
                    System.out.println("Done!");
                    break;
                case 6:
                    // Return all streamers
                    List<Streamer> AllStreamers = StreamerTools.getAllStreamers();
                    UserInterface.printStreamersPerLine(AllStreamers, 8);
                    break;
                case 7:
                    // Search by Streamers (implement as needed)
                    String stremerSearchInput = UserInterface.promptStringInput("Please enter the streamer you wish to search for... ");
                    Streamer searchStreamerReturn = StreamerTools.getStreamerByNameUrl(stremerSearchInput);
                    System.out.println(searchStreamerReturn.toString());
                    
                    break;
                case 8:
                    // Search Streams (implement as needed)
                    break;
                case 9:
                    String streameraddinput = UserInterface.promptStringInput("Please enter the streamer you wish to add... ");
                    DatabaseTools.addStreamer(streameraddinput);
                    TTrackerScraper.scrapeSingleStreamer(streameraddinput);
                    break;
                case 10:
                    String streamerDeleteInput = UserInterface.promptStringInput("Please enter the streamer you wish to delete... ");
                    DatabaseTools.deleteStreamer(streamerDeleteInput);
                    break;
                case 20:
                    System.out.println("Exiting...");
                    return; // Exit the program
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }

    }
}

