package br.com.NexusSaude;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;

public class UsuarioDAO {
    EntityManagerFactory emf = Persistence.createEntityManagerFactory("crud-basic");

    public void salvar(Usuario usuario) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.persist(usuario);
        em.getTransaction().commit();
        em.close();
    }

    public Usuario buscarPorId(String string) {
        EntityManager em = emf.createEntityManager();
        Usuario usuario = em.find(Usuario.class, string);
        em.close();
        return usuario;
    }

    public Usuario buscarPorEmail(String email) {
        EntityManager em = emf.createEntityManager();
        Usuario usuario = em.createQuery("SELECT u FROM Usuario u WHERE u.email = :email", Usuario.class)
                .setParameter("email", email)
                .getSingleResult();
        em.close();
        return usuario;
    }

    public List<Usuario> listarPorTipo(String tipoUsuario) {
        EntityManager em = emf.createEntityManager();
        List<Usuario> usuarios = em.createQuery("SELECT u FROM Usuario u WHERE u.tipoUsuario = :tipo", Usuario.class)
                .setParameter("tipo", tipoUsuario)
                .getResultList();
        em.close();
        return usuarios;
    }

    public void atualizar(Usuario usuario) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.merge(usuario);
        em.getTransaction().commit();
        em.close();
    }

    public void remover(Long id) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        Usuario usuario = em.find(Usuario.class, id);
        if (usuario != null) {
            em.remove(usuario);
        }
        em.getTransaction().commit();
        em.close();
    }
}