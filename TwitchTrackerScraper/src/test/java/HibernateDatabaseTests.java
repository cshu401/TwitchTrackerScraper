import Api.Domain.Streamer;
import Api.Domain.Streams;
import org.junit.jupiter.api.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.time.LocalDateTime;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.PersistenceException;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.DynamicTest.stream;
import org.springframework.boot.test.context.SpringBootTest;
import Api.MainApplication;

@SpringBootTest(classes = MainApplication.class) // Use this annotation to load the full Spring context
//@DataJpaTest
class HibernateDatabaseTests {

    //private static EntityManagerFactory emf;

    @Autowired
    private EntityManager em;

    // @BeforeAll
    // static void setupEntityManagerFactory() {
    //     emf = Persistence.createEntityManagerFactory("MyAppPersistenceUnit"); // Use a separate persistence unit for tests
    // }

    // @BeforeEach
    // void setupEntityManager() {
    //     em = emf.createEntityManager();
    // }

    // @AfterEach
    // void closeEntityManager() {
    //     if (em.isOpen()) {
    //         em.close();
    //     }
    // }

    // @AfterAll
    // static void closeEntityManagerFactory() {
    //     if (emf.isOpen()) {
    //         emf.close();
    //     }
    // }

    @Test
    void testCreateStreamerAndStream() {
        em.getTransaction().begin();
        Streamer newStreamer = new Streamer("test_streamer");
        Streams stream = new Streams("Test Title", LocalDateTime.now(), 0, 0, 0, 0);
        newStreamer.addStream(stream);
        em.persist(newStreamer);
        em.getTransaction().commit();

        Streamer foundStreamer = em.find(Streamer.class, newStreamer.getId());
        assertNotNull(foundStreamer);
        assertEquals("test_streamer", foundStreamer.getNameUrl());
        assertEquals(1, foundStreamer.getStreams().size());
        assertEquals("Test Stream", foundStreamer.getStreams().get(0).getTitle());
    }

    @Test
    void testFindStreamerByNameUrl() {
        em.getTransaction().begin();
        Streamer newStreamer = new Streamer("find_test_streamer");
        em.persist(newStreamer);
        em.getTransaction().commit();

        TypedQuery<Streamer> query = em.createQuery("SELECT s FROM Domain.Streamer s WHERE s.nameUrl = :nameUrl", Streamer.class);
        query.setParameter("nameUrl", "find_test_streamer");
        Streamer foundStreamer = query.getSingleResult();

        assertNotNull(foundStreamer);
        assertEquals("find_test_streamer", foundStreamer.getNameUrl());
    }
    @Test
    void testUpdateStreamer() {
        // Arrange
        em.getTransaction().begin();
        Streamer streamer = new Streamer("update_streamer");
        em.persist(streamer);
        em.getTransaction().commit();

        // Act
        em.getTransaction().begin();
        Streamer foundStreamer = em.find(Streamer.class, streamer.getId());
        foundStreamer.setNameUrl("updated_name");
        em.getTransaction().commit();

        // Assert1
        Streamer updatedStreamer = em.find(Streamer.class, streamer.getId());
        assertNotNull(updatedStreamer);
        assertEquals("updated_name", updatedStreamer.getNameUrl());
    }
    @Test
    void testDeleteStreamer() {
        // Arrange
        em.getTransaction().begin();
        Streamer streamer = new Streamer("delete_streamer");
        em.persist(streamer);
        em.getTransaction().commit();
    
        // Act
        em.getTransaction().begin();
        Streamer foundStreamer = em.find(Streamer.class, streamer.getId());

        System.out.println(foundStreamer);
        
        em.remove(foundStreamer);
        em.flush(); // Ensure the remove operation is synchronized with the database immediately.
        em.getTransaction().commit();
    
        // Assert
        Streamer deletedStreamer = em.find(Streamer.class, streamer.getId());
        assertNull(deletedStreamer);
    }
    
    @Test
    void testTransactionRollback() {
        // Arrange
        em.getTransaction().begin();
        Streamer streamer = new Streamer("rollback_streamer");
        em.persist(streamer);
        em.getTransaction().rollback();

        // Act
        Streamer rolledBackStreamer = em.find(Streamer.class, streamer.getId());

        // Assert
        assertNull(rolledBackStreamer);
    }
    @Test
    void testPersistNullStreamerShouldThrowException() {
        Exception exception = assertThrows(PersistenceException.class, () -> {
            em.getTransaction().begin();
            Streamer streamer = new Streamer(null); // Assuming 'nameUrl' should not be null
            em.persist(streamer);
            em.getTransaction().commit();
        });

        assertTrue(exception.getMessage().contains("could not execute statement"));
    }

    @Test
    void testCascadePersist() {
        // Arrange
        em.getTransaction().begin();
        Streamer streamer = new Streamer("cascade_persist_streamer");
        Streams stream = new Streams("Test Title", LocalDateTime.now(), 0, 0, 0, 0);
        streamer.addStream(stream); // This should cascade the persist operation
        // Do not persist the stream explicitly

        // Act
        em.persist(streamer);
        em.getTransaction().commit();

        // Assert
        Streamer persistedStreamer = em.find(Streamer.class, streamer.getId());
        assertNotNull(persistedStreamer);
        assertEquals(1, persistedStreamer.getStreams().size()); 
        assertNotNull(persistedStreamer.getStreams().get(0).getId()); // The stream should have an ID after persist
    }

    @Test
    void testCascadeDelete() {
        // Arrange
        em.getTransaction().begin();
        Streamer streamer = new Streamer("cascade_delete_streamer");
        Streams stream = new Streams("Test Title", LocalDateTime.now(), 0, 0, 0, 0);
        streamer.addStream(stream);
        em.persist(streamer);
        em.getTransaction().commit();

        Long streamId = stream.getId();

        // Act
        em.getTransaction().begin();
        Streamer foundStreamer = em.find(Streamer.class, streamer.getId());
        em.remove(foundStreamer); // This should cascade the remove operation to the associated streams
        em.getTransaction().commit();

        // Assert
        Streams deletedStream = em.find(Streams.class, streamId);
        assertNull(deletedStream);
    }

    @Test
    public void creatingCustomStreamer() {
        em.getTransaction().begin();
        Streamer newStreamer = new Streamer("ixi_vt");
        em.persist(newStreamer);
        em.getTransaction().commit();
        // Additional assertions or operations
    }






    // More tests for update and delete operations can be added here

}
