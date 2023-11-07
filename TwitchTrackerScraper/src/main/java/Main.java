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


        
        Streamer newStreamer = new Streamer("some_streamer");


        
        // Now, we create an instance of Streams using the constructor
        Streams stream = new Streams("s12345", "Random Stream Title", LocalDateTime.now());

        // Add the stream to the Streamer
        newStreamer.addStream(stream);        

        // Persist the Streamer object
        em.persist(newStreamer);

        // Commit the transaction
        em.getTransaction().commit();

        // Close the EntityManager and EntityManagerFactory
        em.close();


        EntityManager em1 = JPAUtil.getEntityManager();   
        em1.getTransaction().begin();

        // Construct a JPQL query to fetch the Streams associated with a particular Streamer
        TypedQuery<Streamer> query = em1.createQuery("SELECT s FROM Streamer s", Streamer.class);
        List<Streamer> allStreamers = query.getResultList();


        System.out.println("Printing results...");
        for (Streamer stream1 : allStreamers) {
            Streams prevStreams = stream1.getStreams().get(0);
            System.out.println("Stream ID: " + stream1.getId());
            System.out.println("Title: " + stream1.getNameUrl());
            System.out.println("Streams: " + prevStreams.getTitle());
        }





        //List<String> names = TwitchHTMLParser.getTitleFromHtml();
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

