package br.com.NexusSaude;

import jakarta.persistence.EntityManager;

public class Main {
    public static void main(String[] args) {
        EntityManager em = JPAUtil.getEntityManager();
        System.out.println("Conexão estabelecida com sucesso!");
        em.close();
    }
}