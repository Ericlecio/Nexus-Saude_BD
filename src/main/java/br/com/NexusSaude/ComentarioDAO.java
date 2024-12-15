package br.com.NexusSaude;

import jakarta.persistence.EntityManager;
import java.util.List;

public class ComentarioDAO {
    private EntityManager em;

    public ComentarioDAO(EntityManager em) {
        this.em = em;
    }

    public void salvar(Comentario comentario) {
        em.getTransaction().begin();
        em.persist(comentario);
        em.getTransaction().commit();
    }

    public Comentario buscarPorId(Long id) {
        return em.find(Comentario.class, id);
    }

    public List<Comentario> listarPorAvaliacao(Long avaliacaoId) {
        return em.createQuery("SELECT c FROM Comentario c WHERE c.avaliacao.id = :avaliacaoId", Comentario.class)
                .setParameter("avaliacaoId", avaliacaoId)
                .getResultList();
    }

    public void atualizar(Comentario comentario) {
        em.getTransaction().begin();
        em.merge(comentario);
        em.getTransaction().commit();
    }

    public void remover(Long id) {
        em.getTransaction().begin();
        Comentario comentario = em.find(Comentario.class, id);
        if (comentario != null) {
            em.remove(comentario);
        }
        em.getTransaction().commit();
    }

    public List<Comentario> listarTodos() {
        try {
            return em.createQuery("SELECT c FROM Comentario c", Comentario.class).getResultList();
        } catch (Exception e) {
            System.out.println("Erro ao listar comentários: " + e.getMessage());
            return List.of();
        }
    }
}