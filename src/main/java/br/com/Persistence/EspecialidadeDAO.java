package br.com.Persistence;

import jakarta.persistence.EntityManager;
import java.util.List;

import br.com.Entidade.Especialidade;
import br.com.Entidade.JPAUtil;

public class EspecialidadeDAO {

    public void salvar(Especialidade especialidade) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(especialidade);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public Especialidade buscarPorId(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.find(Especialidade.class, id);
        } finally {
            em.close();
        }
    }

    public List<Especialidade> listar() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("FROM Especialidade", Especialidade.class).getResultList();
        } finally {
            em.close();
        }
    }

    public void atualizar(Especialidade especialidade) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(especialidade);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public void remover(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Especialidade especialidade = em.find(Especialidade.class, id);
            if (especialidade != null) {
                em.remove(especialidade);
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
