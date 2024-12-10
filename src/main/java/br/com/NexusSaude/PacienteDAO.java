package br.com.NexusSaude;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;

public class PacienteDAO {
    EntityManagerFactory emf = Persistence.createEntityManagerFactory("crud-basic");

    public void salvar(Paciente paciente) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.persist(paciente);
        em.getTransaction().commit();
        em.close();
    }

    public Paciente buscarPorId(Long id) {
        EntityManager em = emf.createEntityManager();
        Paciente paciente = em.find(Paciente.class, id);
        em.close();
        return paciente;
    }

    public List<Paciente> listar() {
        EntityManager em = emf.createEntityManager();
        List<Paciente> pacientes = em.createQuery("FROM Paciente", Paciente.class).getResultList();
        em.close();
        return pacientes;
    }

    public void atualizar(Paciente paciente) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.merge(paciente);
        em.getTransaction().commit();
        em.close();
    }

    public void remover(Long id) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        Paciente paciente = em.find(Paciente.class, id);
        if (paciente != null) {
            em.remove(paciente);
        }
        em.getTransaction().commit();
        em.close();
    }
}