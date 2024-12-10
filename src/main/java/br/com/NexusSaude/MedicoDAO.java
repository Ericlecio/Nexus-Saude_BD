package br.com.NexusSaude;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;

public class MedicoDAO {
    EntityManagerFactory emf = Persistence.createEntityManagerFactory("crud-basic");

    public void salvar(Medico medico) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.persist(medico);
        em.getTransaction().commit();
        em.close();
    }

    public Medico buscarPorId(Long id) {
        EntityManager em = emf.createEntityManager();
        Medico medico = em.find(Medico.class, id);
        em.close();
        return medico;
    }

    public List<Medico> listar() {
        EntityManager em = emf.createEntityManager();
        List<Medico> medicos = em.createQuery("FROM Medico", Medico.class).getResultList();
        em.close();
        return medicos;
    }

    public List<Medico> listarPorEspecialidade(Long especialidadeId) {
        EntityManager em = emf.createEntityManager();
        List<Medico> medicos = em.createQuery("SELECT m FROM Medico m WHERE m.especialidade.id = :id", Medico.class)
                .setParameter("id", especialidadeId)
                .getResultList();
        em.close();
        return medicos;
    }

    public void atualizar(Medico medico) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.merge(medico);
        em.getTransaction().commit();
        em.close();
    }

    public void remover(Long id) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        Medico medico = em.find(Medico.class, id);
        if (medico != null) {
            em.remove(medico);
        }
        em.getTransaction().commit();
        em.close();
    }
}