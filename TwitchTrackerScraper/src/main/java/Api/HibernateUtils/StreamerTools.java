package Api.HibernateUtils;

import Api.Domain.Streamer;

import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;


import org.springframework.stereotype.Service;

/**
 * This class provides utility methods for working with Domain.Streamer objects.
 */
/**
 * The Utils.StreamerTools class provides utility methods for working with Domain.Streamer objects in the database.
 */
@Service
public class StreamerTools {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Returns a list of all Domain.Streamer objects in the database.
     *
     * @return a list of all Domain.Streamer objects in the database
     */
    public List<Streamer> getAllStreamers() {
        // Create a JPQL query to select all Domain.Streamer objects
        String jpql = "SELECT s FROM Domain.Streamer s";

        // Create the query
        Query query = entityManager.createQuery(jpql);

        // Execute the query and return the results as a list of Domain.Streamer objects
        List<Streamer> streamers = query.getResultList();

        return streamers;
    }

    
    /**
     * This method retrieves a Domain.Streamer object from the database by its nameUrl.
     *
     * @param nameUrl the nameUrl of the Domain.Streamer to retrieve
     * @return the Domain.Streamer object with the given nameUrl, or null if not found
     */
    public Streamer getStreamerByNameUrl(String nameUrl) {
        Streamer streamer = null;
        try {
            // Create a JPQL query to select the Domain.Streamer object with the given nameUrl
            String jpql = "SELECT s FROM Domain.Streamer s WHERE s.nameUrl = :nameUrl";
    
            // Create and execute the query
            Query query = entityManager.createQuery(jpql);
            query.setParameter("nameUrl", nameUrl);
            List<Streamer> results = query.getResultList();
    
            // Check if the result list is not entityManagerpty and get the first Domain.Streamer
            if (!results.isEmpty()) {
                streamer = results.get(0);
            } else {
                // Return an error code if the result list is entityManagerpty
                streamer = new Streamer();
                streamer.setId(-1L);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            entityManager.close();
        }
        return streamer;
    }
    

    /**
     * WARNING: This method should only be used for development purposes.
     * Deletes all data from all tables in the database.
     *
     * @param entityManager the EntityManager to use for the transaction
     */
    public void clearAllTables() {
        EntityTransaction transaction = entityManager.getTransaction();
        boolean wasAlreadyActive = transaction.isActive();
    
        try {
            // Only begin a new transaction if one isn't already active
            if (!wasAlreadyActive) {
                transaction.begin();
            }
    
            // Get the names of all tables
            List<String> tableNames = entityManager.getMetamodel().getEntities().stream()
                    .map(e -> e.getName())
                    .collect(Collectors.toList());
    
            // Disable foreign key checks to prevent constraint violation errors
            entityManager.createNativeQuery("SET FOREIGN_KEY_CHECKS = 0").executeUpdate();
    
            // Truncate each table
            for (String tableName : tableNames) {
                entityManager.createNativeQuery("TRUNCATE TABLE " + tableName).executeUpdate();
            }
    
            // Re-enable foreign key checks
            entityManager.createNativeQuery("SET FOREIGN_KEY_CHECKS = 1").executeUpdate();
    
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
