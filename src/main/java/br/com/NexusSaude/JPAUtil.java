package br.com.NexusSaude;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class JPAUtil {
    private static final EntityManagerFactory emf;

    static {
        try {
            emf = Persistence.createEntityManagerFactory("nexus-saude"); // Certifique-se de usar o nome correto
        } catch (Exception e) {
            throw new ExceptionInInitializerError("Erro ao inicializar EntityManagerFactory: " + e.getMessage());
        }
    }

    public static EntityManager getEntityManager() {
        return emf.createEntityManager();
    }
}
