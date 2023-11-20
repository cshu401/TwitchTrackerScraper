package Api.Client;

import Api.HibernateUtils.StreamerTools;
import Api.Scraper.TTrackerScraper;
import Api.HibernateUtils.DatabaseTools;
import java.util.Scanner;


public class Main {

    static Scanner scanner = new Scanner(System.in);
    private UserInterface userInterface;
    private TTrackerScraper tTrackerScraper;

    public Main() {
        this.userInterface = new UserInterface();
        this.tTrackerScraper = new TTrackerScraper();
    }

    public static void main(String[] args) {
        Main mainApp = new Main();  // Creating an instance of Mainz
        mainApp.runApp();  // Running the application
    }

    private void runApp() {
        StreamerTools streamerTools = new StreamerTools();

        while (true) {
            userInterface.displayMenu();
            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    TTrackerScraper.scrapeWholeHTML();
                    System.out.println("Update complete!!!");
                    break;
                case 2:
                    // Add Domain.Streamer
                    String input = UserInterface.promptStringInput("Please enter the name of the streamer you wish to add: ");
                    DatabaseTools.addStreamer(input);
                    break;
                case 3:
                    // Delete Domain.Streamer
                    String input1 = UserInterface.promptStringInput("Please enter the name of the streamer you wish to delete: ");
                    DatabaseTools.deleteStreamer(input1);
                    break;
                case 4:
                    //TTrackerScraper.scrapeAllStreamers(scraper);
                    break;
                case 5:
                    System.out.println("Exporting to Excel...");
                    ExcelExporter.exportToExcel();
                    System.out.println("Done!");
                    break;
                case 6:
                    // Return all streamers
                    // List<Streamer> AllStreamers = StreamerTools.getAllStreamers();
                    // UserInterface.printStreamersPerLine(AllStreamers, 8);
                    break;
                case 7:
                    // Search by Streamers (implement as needed)
                    // String stremerSearchInput = UserInterface.promptStringInput("Please enter the streamer you wish to search for... ");
                    // Streamer searchStreamerReturn = StreamerTools.getStreamerByNameUrl(stremerSearchInput);
                    // System.out.println(searchStreamerReturn.toString());
                    
                    break;
                case 8:
                    // Search Domain.Streams (implement as needed)
                    break;
                case 9:
                    // String streameraddinput = UserInterface.promptStringInput("Please enter the streamer you wish to add... ");
                    // DatabaseTools.addStreamer(streameraddinput);
                    // TTrackerScraper.scrapeSingleStreamer(streameraddinput);
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



