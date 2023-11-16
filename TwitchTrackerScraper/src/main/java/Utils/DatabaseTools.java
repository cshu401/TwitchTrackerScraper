package Utils;

import Domain.Streamer;
import Utils.JPAUtil;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;

public class DatabaseTools {

    public static boolean deleteStreamer(String streamerUrl) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction transaction = em.getTransaction();

        try {
            transaction.begin();

            TypedQuery<Streamer> query = em.createQuery("SELECT s FROM Domain.Streamer s WHERE s.nameUrl = :nameUrl", Streamer.class);
            query.setParameter("nameUrl", streamerUrl);
            List<Streamer> existingStreamers = query.getResultList();
            if (existingStreamers.get(0) == null) {
                System.out.println("Domain.Streamer not found with ID: " + streamerUrl);
                return false;
            }

            em.remove(existingStreamers.get(0));

            transaction.commit();
            System.out.println("Domain.Streamer deleted successfully.");
            return true;
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            e.printStackTrace();
            return false;
        } finally {
            em.close();
        }
    }

    
    public static boolean addStreamer(String streamerUrl) {
        if (streamerUrl == null || streamerUrl.isEmpty()) {
            System.out.println("Domain.Streamer URL cannot be null or empty.");
            return false;
        }

        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction transaction = em.getTransaction();

        try {
            transaction.begin();
            List<String> streamerNames = new ArrayList<>();
            streamerNames.add(streamerUrl);
            TTrackerScraper.createOrUpdateStreamers(streamerNames, em);
            return true;
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            e.printStackTrace();
            return false;
        } finally {
            em.close();
        }
    }

    
}
