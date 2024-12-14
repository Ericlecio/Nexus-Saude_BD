package br.com.NexusSaude;

import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Scanner;

public class PagamentoDAO {
    private EntityManager em;

    public PagamentoDAO(EntityManager em) {
        this.em = em;
    }

    public void cadastrar(Scanner scanner) {
        System.out.println("\n### CADASTRAR PAGAMENTO ###");

        System.out.print("Digite o ID da consulta relacionada ao pagamento: ");
        long consultaId = scanner.nextLong();
        scanner.nextLine();

        Consulta consulta = em.find(Consulta.class, consultaId);
        if (consulta == null) {
            System.out.println("Consulta não encontrada! Cadastre uma consulta antes.");
            return;
        }

        System.out.print("Digite o valor pago: ");
        double valorPago = scanner.nextDouble();
        scanner.nextLine();

        System.out.print("Digite a forma de pagamento (Cartão, Dinheiro, etc.): ");
        String formaPagamento = scanner.nextLine();

        try {
            em.getTransaction().begin();

            Pagamento pagamento = new Pagamento();
            pagamento.setConsulta(consulta);
            pagamento.setValorPago(valorPago);
            pagamento.setFormaPagamento(formaPagamento);
            pagamento.setStatus("Pago");
            pagamento.setDataPagamento(new java.sql.Date(System.currentTimeMillis()));

            em.persist(pagamento);

            consulta.setStatus("Paga");
            em.merge(consulta);

            em.getTransaction().commit();
            System.out.println("Pagamento cadastrado com sucesso! ID: " + pagamento.getId());
        } catch (Exception e) {
            em.getTransaction().rollback();
            System.out.println("Erro ao cadastrar o pagamento: " + e.getMessage());
        }
    }

    public void listar() {
        System.out.println("\n### LISTAR PAGAMENTOS ###");
        List<Pagamento> pagamentos = em.createQuery("SELECT p FROM Pagamento p", Pagamento.class).getResultList();
        if (pagamentos.isEmpty()) {
            System.out.println("Nenhum pagamento encontrado.");
        } else {
            for (Pagamento pagamento : pagamentos) {
                System.out.println("ID: " + pagamento.getId() + 
                                   ", Consulta ID: " + pagamento.getConsulta().getId() +
                                   ", Valor Pago: " + pagamento.getValorPago() +
                                   ", Forma de Pagamento: " + pagamento.getFormaPagamento() +
                                   ", Status: " + pagamento.getStatus() +
                                   ", Data: " + pagamento.getDataPagamento());
            }
        }
    }

    public void salvar(Pagamento pagamento) {
        try {
            em.getTransaction().begin();
            em.persist(pagamento);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            System.out.println("Erro ao salvar pagamento: " + e.getMessage());
        }
    }
}
