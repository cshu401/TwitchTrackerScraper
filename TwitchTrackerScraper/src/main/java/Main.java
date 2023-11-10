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

    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {

        //scrapeWholeDoc();
        TTrackerScraper scraper = new TTrackerScraper();

        while (true) {
            displayMenu();
            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    scrapeWholeDoc();
                    System.out.println("Update complete!!!");
                    break;
                case 2:
                    extractToExcel();
                case 3:
                    scrapeAllStreamers(scraper);
                case 4:
                    boolean jpqlMenuActive = true;
                    while (jpqlMenuActive) {
                        displayDatabaseMenu();
                        int jpqlChoice = scanner.nextInt();
                        switch (jpqlChoice) {
                            case 1:
                                // Implement search by Name URL
                                String nameInput = promptStringInput("Please enter a Url to search for");
                                Streamer output = StreamerTools.getStreamerByNameUrl(nameInput);
                                displayStreamerData(output);
                                
                                break;
                            case 2:
                                // return all streamers
                                List<Streamer> streamers = StreamerTools.getAllStreamers();
                                System.out.println("Printing all Streamers...");
                                printStreamersPerLine(streamers,6);
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




        // List<Streamer> streamers = StreamerTools.getAllStreamers();


        // for (Streamer streamer : streamers) {
        //     System.out.println(streamer.getNameUrl());
        // }

        // Streamer streamget = StreamerTools.getStreamerByNameUrl("jmjdoc");

        // System.out.println(streamget.getNameUrl());
    }

    private static void displayMenu() {

        int sizeChecker = StreamerTools.getAllStreamers().size();
        // Top Border
        System.out.println("░▀█▀░█░█░▀█▀░▀█▀░█▀▀░█░█░░░▀█▀░█▀▄░█▀█░█▀▀░█░█░█▀▀░█▀▄░░░█▀▀░█▀▀░█▀▄░█▀█░█▀█░█▀▀░█▀▄");
        System.out.println("░░█░░█▄█░░█░░░█░░█░░░█▀█░░░░█░░█▀▄░█▀█░█░░░█▀▄░█▀▀░█▀▄░░░▀▀█░█░░░█▀▄░█▀█░█▀▀░█▀▀░█▀▄");
        System.out.println("░░▀░░▀░▀░▀▀▀░░▀░░▀▀▀░▀░▀░░░░▀░░▀░▀░▀░▀░▀▀▀░▀░▀░▀▀▀░▀░▀░░░▀▀▀░▀▀▀░▀░▀░▀░▀░▀░░░▀▀▀░▀░▀");
        System.out.println(new String(new char[50]).replace("\0", "-"));
        System.out.println(new String(new char[50]).replace("\0", "-"));
    
        //Database size
        
        System.out.println("The database is currently populated by " +sizeChecker+" streamers \n");

        // Menu Options
        System.out.println("1. Scrape Whole Document");
        System.out.println("2. Extract Data to Excel");
        System.out.println("3. Update Database");
        System.out.println("4. Display Database");
    
        // Bottom Border
        System.out.println(new String(new char[50]).replace("\0", "-"));
        System.out.print("Enter your choice: ");
    }


    private static void displayDatabaseMenu() {
        System.out.println("\n--- JPQL Search Operations ---");
        System.out.println("1. Search by Name URL");
        System.out.println("2. Return all streamers");
        System.out.println("3. Search by Followers");
        System.out.println("4. Return to Main Menu");
        System.out.print("Enter your choice: ");
    }

    private static void displayStreamerData(Streamer streamer){
        System.out.println("NameUrl: " + streamer.getNameUrl());
        System.out.println("Followers: " + streamer.getFollowers());
        System.out.println("Average Viewers: " + streamer.getAverageViewers());
        System.out.println("Peak Viewers: " + streamer.getPeakViewers());
        System.out.println("Hours Watched: " + streamer.getHoursWatched());
        System.out.println("Followers Per Hour: " + streamer.getFollowersPerHour());
        System.out.println("Minutes Streamed: " + streamer.getMinutesStreamed());
        System.out.println("Last scraped: " + streamer.getLastScraped());

    }


    public static String promptStringInput(String message) {
        System.out.print(message + ": ");
        scanner.nextLine(); // Consume any leftover newline character
        return scanner.nextLine(); // Wait for and return the user's input
    }

    private static void printStreamersPerLine(List<Streamer> streamers, int wordsPerLine) {
        int count = 0;
        for (Streamer streamer : streamers) {
            System.out.print(streamer.getNameUrl() + ", ");
            count++;

            if (count % wordsPerLine == 0) {
                System.out.println();
            }
        }

        if (count % wordsPerLine != 0) {
            System.out.println();
        }
    }
    
        

    private static void extractToExcel(){

    }


    public static void scrapeWholeDoc(){
        EntityManager em = JPAUtil.getEntityManager();  
        try {
            em.getTransaction().begin();
    
            List<String> streamerNames = TwitchHTMLParser.getUsernames("TwitchHTML//twitch.htm");
            createOrUpdateStreamers(streamerNames, em);  // Modified method for creating/updating streamers
    
            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
        } finally {
            em.close();
        }
    
        TTrackerScraper scraper = new TTrackerScraper();
        scrapeAllStreamers(scraper);  // This method needs to handle its own EntityManager
    }


    public static void createOrUpdateStreamers(List<String> streamerNames, EntityManager em) {
        for (String nameUrl : streamerNames) {
            String jpql = "SELECT s FROM Streamer s WHERE s.nameUrl = :nameUrl";
            List<Streamer> existingStreamers = em.createQuery(jpql, Streamer.class)
                                                 .setParameter("nameUrl", nameUrl)
                                                 .getResultList();
    
            if (existingStreamers.isEmpty()) {
                Streamer newStreamer = new Streamer();
                newStreamer.setNameUrl(nameUrl);
                // Set other properties of newStreamer as needed
                em.persist(newStreamer);
            } else {
                // If a Streamer with this nameUrl already exists, you can update it
                Streamer existingStreamer = existingStreamers.get(0);
                // Update properties of existingStreamer as needed
            }
        }
    }
    

       
    public static void scrapeAllStreamers(TTrackerScraper scraper ){
        List<Streamer> streamers = StreamerTools.getAllStreamers();
        for (Streamer streamer : streamers) {
            scraper.scrapeBasicDetails(streamer);
        }

    }

        




    

}

