package br.com.NexusSaude;

import jakarta.persistence.EntityManager;
import java.util.List;

public class ConsultaDAO {
    public void salvar(Consulta consulta) {
        EntityManager em = (EntityManager) JPAUtil.getEntityManager();
        em.getTransaction().begin();
        em.persist(consulta);
        em.getTransaction().commit();
        em.close();
    }

    public Consulta buscarPorId(Long id) {
        EntityManager em = (EntityManager) JPAUtil.getEntityManager();
        Consulta consulta = em.find(Consulta.class, id);
        em.close();
        return consulta;
    }

    public List<Consulta> listar() {
        EntityManager em = (EntityManager) JPAUtil.getEntityManager();
        List<Consulta> consultas = em.createQuery("FROM Consulta", Consulta.class).getResultList();
        em.close();
        return consultas;
    }

    public void atualizar(Consulta consulta) {
        EntityManager em = (EntityManager) JPAUtil.getEntityManager();
        em.getTransaction().begin();
        em.merge(consulta);
        em.getTransaction().commit();
        em.close();
    }

    public void remover(Long id) {
        EntityManager em = (EntityManager) JPAUtil.getEntityManager();
        em.getTransaction().begin();
        Consulta consulta = em.find(Consulta.class, id);
        if (consulta != null) {
            em.remove(consulta);
        }
        em.getTransaction().commit();
        em.close();
    }
}
