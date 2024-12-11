package br.com.NexusSaude;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.List;

public class MedicoDAO {
    EntityManagerFactory emf = Persistence.createEntityManagerFactory("crud-basic");

    public void salvar(Medico medico) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(medico);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public Medico buscarPorId(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(Medico.class, id);
        } finally {
            em.close();
        }
    }

    public List<Medico> listar() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("FROM Medico", Medico.class).getResultList();
        } finally {
            em.close();
        }
    }

    public List<Medico> listarPorEspecialidade(Long especialidadeId) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT m FROM Medico m WHERE m.especialidade.id = :id", Medico.class)
                    .setParameter("id", especialidadeId)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public void atualizar(Medico medico) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(medico);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public void remover(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Medico medico = em.find(Medico.class, id);
            if (medico != null) {
                em.remove(medico);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }
}
