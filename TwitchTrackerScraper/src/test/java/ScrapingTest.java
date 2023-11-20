import Api.Domain.Streamer;
import Api.Domain.Streams;
import Api.Scraper.TTrackerScraper;
import org.junit.jupiter.api.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ScrapingTest {

    private static EntityManagerFactory emf;
    private EntityManager em;

    @BeforeAll
    static void setupEntityManagerFactory() {
        emf = Persistence.createEntityManagerFactory("MyAppPersistenceUnit"); // Use a separate persistence unit for tests
    }

    @BeforeEach
    void setupEntityManager() {
        em = emf.createEntityManager();
    }

    @AfterEach
    void closeEntityManager() {
        if (em.isOpen()) {
            em.close();
        }
    }

    @AfterAll
    static void closeEntityManagerFactory() {
        if (emf.isOpen()) {
            emf.close();
        }
    }

    @Test
    void testScrapeStreams(){
        //create a streamer called ixivt
        Streamer ixivt = new Streamer("mizkif"); // Make sure the Domain.Streamer class is defined and has a constructor that takes a string
        TTrackerScraper scraper = new TTrackerScraper(); // Make sure the Scraper.TTrackerScraper class is defined and has the scrapeAllStreams method
        scraper.scrapeAllStreams(ixivt); // Assuming scrapeAllStreams returns a List of Stream objects
    }

    @Test
    void testScrapeAndQueryStreams() {
        // Assuming em is your EntityManager instance
        EntityManager em = emf.createEntityManager();
        try {
            // Begin transaction
            em.getTransaction().begin();

            // Create a streamer called mizkif and persist it
            Streamer ixivt = new Streamer("ixivt");
            em.persist(ixivt);

            // Commit the transaction to save the streamer
            em.getTransaction().commit();

            // Instantiate the scraper and scrape all streams
            TTrackerScraper scraper = new TTrackerScraper();
            scraper.scrapeAllStreams(ixivt); // This method should commit the streams to the database

            // Query the database for streams associated with mizkif
            TypedQuery<Streams> query = em.createQuery("SELECT s FROM Domain.Streams s WHERE s.streamer.nameUrl = :nameUrl", Streams.class);
            query.setParameter("nameUrl", "ixivt");
            List<Streams> resultStreams = query.getResultList();

            // Assertions to verify the scraping and querying was successful
            assertNotNull(resultStreams, "The list of queried streams should not be null");
            assertFalse(resultStreams.isEmpty(), "The list of queried streams should not be empty");

            // You could also add more detailed checks to verify the contents of the queried streams
            // For example, you might want to check that all stream objects have non-null and valid dates
            assertTrue(resultStreams.stream().allMatch(stream -> stream.getDate() != null), "All queried streams should have non-null dates");

            for (Streams stream : resultStreams) {
            System.out.println(stream.getTitle());
            }

        } finally {
            // Clean up the EntityManager
            if (em.isOpen()) {
                em.close();
            }
        }
    }




    // More tests for update and delete operations can be added here

}
