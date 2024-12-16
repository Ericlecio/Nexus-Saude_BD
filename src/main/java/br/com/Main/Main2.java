package br.com.Main;

import jakarta.persistence.EntityManager;

import java.util.Scanner;

import br.com.Entidade.JPAUtil;
import br.com.Persistence.ConsultaDAO;
import br.com.Persistence.MedicoDAO;
import br.com.Persistence.PacienteDAO;
import br.com.Persistence.PagamentoDAO;

public class Main2 {

    public static void main(String[] args) {
        EntityManager em = JPAUtil.getEntityManager();
        Scanner scanner = new Scanner(System.in);

        try {
            menuAgendamento(em, scanner);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Erro no sistema de agendamento.");
        } finally {
            em.close();
            scanner.close();
        }
    }

    public static void menuAgendamento(EntityManager em, Scanner scanner) {
        ConsultaDAO consultaDAO = new ConsultaDAO(em);
        PagamentoDAO pagamentoDAO = new PagamentoDAO(em);
        MedicoDAO medicoDAO = new MedicoDAO(em);
        PacienteDAO pacienteDAO = new PacienteDAO(em);

        boolean continuar = true;

        while (continuar) {
            System.out.println("\n### MENU AGENDAMENTO ###");
            System.out.println("1. Agendar Consulta");
            System.out.println("2. Listar Consultas");
            System.out.println("3. Atualizar Consulta");
            System.out.println("4. Remover Consulta");
            System.out.println("5. Realizar Pagamento");
            System.out.println("6. Listar Pagamentos");
            System.out.println("7. Atualizar Pagamento");
            System.out.println("8. Remover Pagamento");
            System.out.println("9. Voltar");
            System.out.print("Escolha uma opção: ");

            try {
                int opcao = scanner.nextInt();
                scanner.nextLine();

                switch (opcao) {
                    case 1:
                    	consultaDAO.agendarConsulta(scanner, medicoDAO, pacienteDAO);
                        break;
                    case 2:
                        consultaDAO.listar();
                        break;
                    case 3:
                    	consultaDAO.atualizar(scanner);
                        break;
                    case 4:
                    	consultaDAO.remover(scanner);
                        break;
                    case 5:
                    	pagamentoDAO.realizarPagamento(scanner, consultaDAO);
                        break;
                    case 6:
                        pagamentoDAO.listar();
                        break;
                    case 7:
                    	pagamentoDAO.atualizarPagamento(scanner);
                        break;
                    case 8:
                    	System.out.print("Digite o ID do pagamento que deseja remover: ");
                        long pagamentoId = scanner.nextLong();
                        scanner.nextLine();
                        pagamentoDAO.removerPagamento(pagamentoId);
                        break;
                    case 9:
                        continuar = false;
                        break;
                    default:
                        System.out.println("Opção inválida! Tente novamente.");
                }
            } catch (Exception e) {
                System.out.println("Entrada inválida! Por favor, tente novamente.");
                scanner.nextLine();
            }
        }
    }
}