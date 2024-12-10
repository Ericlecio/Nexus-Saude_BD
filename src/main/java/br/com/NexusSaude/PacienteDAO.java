package br.com.NexusSaude;

import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

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