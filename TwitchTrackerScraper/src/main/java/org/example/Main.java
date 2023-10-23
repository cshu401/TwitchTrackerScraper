package org.example;
import javax.persistence.*;


// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    public static void main(String[] args) {
        EntityManager em =  JPAUtil.getEntityManager();

        em.getTransaction().begin();

        Streamer streamer1 = new Streamer("name1");
        em.persist(streamer1);
        em.getTransaction().commit();
        em.close();

        //Test retrieve
        em = JPAUtil.getEntityManager();
        em.getTransaction().begin();
        TypedQuery<Streamer> query = em.createQuery("SELECT s FROM Streamer s WHERE s.nameUrl = :name", Streamer.class);
        query.setParameter("name", "name1");
        Streamer retrievedStreamer = query.getSingleResult();

        System.out.println("Retrieved streamer: " + retrievedStreamer.getName());

        System.out.println("Process done");
    }
}