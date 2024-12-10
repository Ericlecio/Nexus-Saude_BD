package br.com.NexusSaude;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;

public class PagamentoDAO {
    EntityManagerFactory emf = Persistence.createEntityManagerFactory("crud-basic");

    public void salvar(Pagamento pagamento) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.persist(pagamento);
        em.getTransaction().commit();
        em.close();
    }

    public Pagamento buscarPorId(Long id) {
        EntityManager em = emf.createEntityManager();
        Pagamento pagamento = em.find(Pagamento.class, id);
        em.close();
        return pagamento;
    }

    public List<Pagamento> listar() {
        EntityManager em = emf.createEntityManager();
        List<Pagamento> pagamentos = em.createQuery("FROM Pagamento", Pagamento.class).getResultList();
        em.close();
        return pagamentos;
    }

    public void atualizar(Pagamento pagamento) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.merge(pagamento);
        em.getTransaction().commit();
        em.close();
    }

    public void remover(Long id) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        Pagamento pagamento = em.find(Pagamento.class, id);
        if (pagamento != null) {
            em.remove(pagamento);
        }
        em.getTransaction().commit();
        em.close();
    }
}