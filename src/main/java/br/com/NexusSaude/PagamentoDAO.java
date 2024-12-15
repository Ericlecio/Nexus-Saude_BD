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

    public void atualizarPagamento(Scanner scanner) {
        System.out.println("\n### ATUALIZAR PAGAMENTO ###");
        System.out.print("Digite o ID do pagamento que deseja atualizar: ");
        long pagamentoId = scanner.nextLong();
        scanner.nextLine();

        Pagamento pagamento = em.find(Pagamento.class, pagamentoId);
        if (pagamento == null) {
            System.out.println("Pagamento não encontrado.");
            return;
        }

        System.out.print("Digite o novo valor pago: ");
        double novoValor = scanner.nextDouble();
        scanner.nextLine();
        pagamento.setValorPago(novoValor);

        System.out.print("Digite a nova forma de pagamento (Cartão, Dinheiro, etc.): ");
        String novaForma = scanner.nextLine();
        pagamento.setFormaPagamento(novaForma);

        try {
            em.getTransaction().begin();
            em.merge(pagamento);
            em.getTransaction().commit();
            System.out.println("Pagamento atualizado com sucesso! ID: " + pagamento.getId());
        } catch (Exception e) {
            em.getTransaction().rollback();
            System.out.println("Erro ao atualizar pagamento: " + e.getMessage());
        }
    }

    public void removerPagamento(Long pagamentoId) {
        try {
            em.getTransaction().begin();
            Pagamento pagamento = em.find(Pagamento.class, pagamentoId);
            if (pagamento != null) {
                Consulta consulta = pagamento.getConsulta();
                if (consulta != null) {
                    consulta.setStatus("Agendada");
                    em.merge(consulta);
                }
                em.remove(pagamento);
                em.getTransaction().commit();
                System.out.println("Pagamento removido com sucesso! ID: " + pagamentoId);
            } else {
                em.getTransaction().rollback();
                System.out.println("Pagamento com ID " + pagamentoId + " não encontrado.");
            }
        } catch (Exception e) {
            em.getTransaction().rollback();
            System.out.println("Erro ao remover pagamento: " + e.getMessage());
        }
    }
    
    public void realizarPagamento(Scanner scanner, ConsultaDAO consultaDAO) {
        System.out.println("\n### REALIZAR PAGAMENTO ###");

        List<Consulta> consultas = consultaDAO.listar();
        if (consultas.isEmpty()) {
            System.out.println("Nenhuma consulta disponível para pagamento.");
            return;
        }

        System.out.print("Digite o ID da consulta para realizar o pagamento: ");
        long consultaId = scanner.nextLong();
        scanner.nextLine();

        Consulta consulta = em.find(Consulta.class, consultaId);
        if (consulta == null || !"Agendada".equals(consulta.getStatus())) {
            System.out.println("Consulta inválida ou já paga. Operação cancelada.");
            return;
        }

        System.out.print("Digite o valor do pagamento: ");
        double valorPago = scanner.nextDouble();
        scanner.nextLine();

        System.out.print("Digite a forma de pagamento (Cartão, Dinheiro, PIX): ");
        String formaPagamento = scanner.nextLine();
        
        Pagamento pagamento = new Pagamento();
        pagamento.setConsulta(consulta);
        pagamento.setValorPago(valorPago);
        pagamento.setFormaPagamento(formaPagamento);
        pagamento.setStatus("Pago");
        pagamento.setDataPagamento(new java.sql.Date(System.currentTimeMillis()));

        try {
            em.getTransaction().begin();
            em.persist(pagamento);
            consulta.setStatus("Paga");
            em.merge(consulta);
            em.getTransaction().commit();
            System.out.println("Pagamento realizado com sucesso! ID do pagamento: " + pagamento.getId());
        } catch (Exception e) {
            em.getTransaction().rollback();
            System.out.println("Erro ao realizar pagamento: " + e.getMessage());
        }
    }

}
