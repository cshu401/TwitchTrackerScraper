import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.*;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;




// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    public static void main(String[] args) {
        EntityManager em = JPAUtil.getEntityManager();  
        em.getTransaction().begin();
        
        clearAllTables(em);

        TTrackerScraper scraper = new TTrackerScraper();

        Streamer testStreamer = new Streamer("kanae_2434");

        em.persist(testStreamer);
        em.getTransaction().commit();

        System.out.println(getAllStreamers().size());

        scraper.scrapeBasicDetails(testStreamer);

        em.close();


        scraper.scrapeBasicDetails(testStreamer);

        List<Streamer> streamers = getAllStreamers();

        System.out.println("size: " + streamers.size());

        System.out.println("streamer: "+ streamers.get(0).getNameUrl());
        System.out.println("getFollowers: "+ streamers.get(0).getFollowers());
        System.out.println("getMinutesStreamed: "+ streamers.get(0).getMinutesStreamed());
        System.out.println("getAverageViewers: "+ streamers.get(0).getAverageViewers());
        System.out.println("getPeakViewers: "+ streamers.get(0).getPeakViewers());

//          List<String> streamerNames = TwitchHTMLParser.getTitleFromHtml();
//         createStreamerFromNames(streamerNames);
    }
        

       

        

        


    public static void createStreamerFromNames(List<String> streamerNames ){
        EntityManager em = JPAUtil.getEntityManager();  
        em.getTransaction().begin();

        for (String streamerName : streamerNames) {
            Streamer streamer = new Streamer(streamerName);
            em.persist(streamer);
        }

        em.getTransaction().commit();

        TypedQuery<Streamer> query = em.createQuery("SELECT s FROM Streamer s", Streamer.class);
        List<Streamer> reteNames = query.getResultList();

        System.out.println("restSize: "+ reteNames.size());

        em.close();

    }

    public static List<Streamer> getAllStreamers() {
        // Create a JPQL query to select all Streamer objects
        String jpql = "SELECT s FROM Streamer s";

        EntityManager em = JPAUtil.getEntityManager();  

        // Create the query
        Query query = em.createQuery(jpql);

        // Execute the query and return the results as a list of Streamer objects
        List<Streamer> streamers = query.getResultList();

        return streamers;
    }




    public static void clearAllTables(EntityManager em) {
        EntityTransaction transaction = em.getTransaction();
        boolean wasAlreadyActive = transaction.isActive();
    
        try {
            // Only begin a new transaction if one isn't already active
            if (!wasAlreadyActive) {
                transaction.begin();
            }
    
            // Get the names of all tables
            List<String> tableNames = em.getMetamodel().getEntities().stream()
                    .map(e -> e.getName())
                    .collect(Collectors.toList());
    
            // Disable foreign key checks to prevent constraint violation errors
            em.createNativeQuery("SET FOREIGN_KEY_CHECKS = 0").executeUpdate();
    
            // Truncate each table
            for (String tableName : tableNames) {
                em.createNativeQuery("TRUNCATE TABLE " + tableName).executeUpdate();
            }
    
            // Re-enable foreign key checks
            em.createNativeQuery("SET FOREIGN_KEY_CHECKS = 1").executeUpdate();
    
            // Only commit if we've started the transaction in this method
            if (!wasAlreadyActive) {
                transaction.commit();
            }
        } catch (Exception e) {
            // The rollback should only occur if the transaction was started in this method
            if (transaction.isActive() && !wasAlreadyActive) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }
    

}

