package br.com.NexusSaude;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class Main2 {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("nexus-saude");
        EntityManager em = emf.createEntityManager();

        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("### INICIANDO OPERAÇÕES COM PAGAMENTOS ###");

            // === CRIAR DADOS INICIAIS (GARANTIR FUNCIONAMENTO) ===
            criarDadosIniciais(em);

            // === MENU PRINCIPAL ===
            boolean continuar = true;
            while (continuar) {
                try {
                    System.out.println("\n### MENU ###");
                    System.out.println("1. Inserir Pagamento");
                    System.out.println("2. Atualizar Pagamento");
                    System.out.println("3. Consultar Pagamentos");
                    System.out.println("4. Deletar Pagamento");
                    System.out.println("5. Sair");
                    System.out.print("Escolha uma opção: ");
                    int opcao = scanner.nextInt();
                    scanner.nextLine(); // Limpar buffer

                    switch (opcao) {
                    case 1:
                        inserirPagamento(em, scanner);
                        break;
                    case 2:
                        atualizarPagamento(em, scanner);
                        break;
                    case 3:
                        consultarPagamentos(em);
                        break;
                    case 4:
                        deletarPagamento(em, scanner);
                        break;
                    case 5:
                        continuar = false;
                        break;
                    default:
                        System.out.println("Opção inválida!");
                        break;
                }
                } catch (InputMismatchException e) {
                    System.out.println("Entrada inválida! Tente novamente.");
                    scanner.nextLine(); // Limpar buffer
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Ocorreu um erro no sistema.");
        } finally {
            em.close();
            emf.close();
            System.out.println("\nConexão encerrada.");
        }
    }

    private static void criarDadosIniciais(EntityManager em) {
        try {
            em.getTransaction().begin();

            System.out.println("Criando dados iniciais...");

            Especialidade especialidade = new Especialidade();
            especialidade.setNome("Cardiologia");
            em.persist(especialidade);

            Usuario medicoUsuario = new Usuario();
            medicoUsuario.setNome("Dr. João");
            medicoUsuario.setEmail("joao@exemplo.com");
            medicoUsuario.setSenha("1234");
            medicoUsuario.setTipoUsuario("medico");
            medicoUsuario.setStatus("ativo");
            em.persist(medicoUsuario);

            Medico medico = new Medico();
            medico.setUsuario(medicoUsuario);
            medico.setCrm("123456");
            medico.setEspecialidade(especialidade);
            medico.setHorariosDisponiveis("8h - 12h");
            em.persist(medico);

            Usuario pacienteUsuario = new Usuario();
            pacienteUsuario.setNome("Maria");
            pacienteUsuario.setEmail("maria@exemplo.com");
            pacienteUsuario.setSenha("1234");
            pacienteUsuario.setTipoUsuario("paciente");
            pacienteUsuario.setStatus("ativo");
            em.persist(pacienteUsuario);

            Paciente paciente = new Paciente();
            paciente.setUsuario(pacienteUsuario);
            paciente.setDataRegistro(LocalDate.now());
            em.persist(paciente);

            Consulta consultaInicial = new Consulta();
            consultaInicial.setEspecialidade(especialidade);
            consultaInicial.setMedico(medico);
            consultaInicial.setPaciente(paciente);
            consultaInicial.setDataConsulta(LocalDateTime.now().plusDays(7));
            consultaInicial.setValor(200.00);
            consultaInicial.setStatus("Agendada");
            em.persist(consultaInicial);

            em.getTransaction().commit();
            System.out.println("Dados iniciais criados com sucesso!");
        } catch (Exception e) {
            em.getTransaction().rollback();
            System.out.println("Erro ao criar dados iniciais: " + e.getMessage());
        }
    }

    private static void inserirPagamento(EntityManager em, Scanner scanner) {
        System.out.println("\n### INSERIR PAGAMENTO ###");

        List<Consulta> consultas = em.createQuery("SELECT c FROM Consulta c", Consulta.class).getResultList();
        if (consultas.isEmpty()) {
            System.out.println("Nenhuma consulta disponível. Crie uma consulta antes de registrar um pagamento.");
            return;
        }

        System.out.println("Consultas disponíveis:");
        for (Consulta c : consultas) {
            System.out.println("ID: " + c.getId() + ", Data: " + c.getDataConsulta() + ", Valor: " + c.getValor());
        }

        Consulta consultaAssociada = null;
        while (consultaAssociada == null) {
            try {
                System.out.print("Digite o ID da consulta para associar ao pagamento: ");
                long consultaId = scanner.nextLong();
                scanner.nextLine(); // Limpar buffer
                consultaAssociada = em.find(Consulta.class, consultaId);
                if (consultaAssociada == null) {
                    System.out.println("Consulta não encontrada. Tente novamente.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Entrada inválida! Por favor, insira um número.");
                scanner.nextLine();
            }
        }

        double valorPagamento = 0;
        while (valorPagamento <= 0) {
            try {
                System.out.print("Digite o valor do pagamento: ");
                valorPagamento = scanner.nextDouble();
                scanner.nextLine();
                if (valorPagamento <= 0) {
                    System.out.println("O valor deve ser maior que zero.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Entrada inválida! Por favor, insira um valor numérico.");
                scanner.nextLine();
            }
        }

        System.out.print("Digite a forma de pagamento (ex.: Cartão, Pix, Boleto): ");
        String formaPagamento = scanner.nextLine();

        em.getTransaction().begin();
        Pagamento pagamento = new Pagamento();
        pagamento.setConsulta(consultaAssociada);
        pagamento.setDataPagamento(LocalDate.now());
        pagamento.setValorPago(valorPagamento);
        pagamento.setFormaPagamento(formaPagamento);
        pagamento.setStatus("Confirmado");
        em.persist(pagamento);
        em.getTransaction().commit();

        System.out.println("Pagamento registrado com sucesso! ID: " + pagamento.getId());
    }
    
    private static void atualizarPagamento(EntityManager em, Scanner scanner) {
        System.out.println("\n### ATUALIZAR PAGAMENTO ###");

        System.out.print("Digite o ID do pagamento para atualizar: ");
        long pagamentoId = scanner.nextLong();
        scanner.nextLine();

        Pagamento pagamento = em.find(Pagamento.class, pagamentoId);
        if (pagamento == null) {
            System.out.println("Pagamento não encontrado.");
            return;
        }

        System.out.println("Pagamento atual: ID " + pagamento.getId() + ", Valor: " + pagamento.getValorPago() +
                ", Forma: " + pagamento.getFormaPagamento() + ", Status: " + pagamento.getStatus());

        System.out.print("Digite o novo status do pagamento (ex.: Confirmado, Pendente, Cancelado): ");
        String novoStatus = scanner.nextLine();

        em.getTransaction().begin();
        pagamento.setStatus(novoStatus);
        em.merge(pagamento);
        em.getTransaction().commit();

        System.out.println("Pagamento atualizado com sucesso! Novo status: " + pagamento.getStatus());
    }

    private static void consultarPagamentos(EntityManager em) {
        System.out.println("\n### CONSULTAR PAGAMENTOS ###");

        List<Pagamento> pagamentos = em.createQuery("SELECT p FROM Pagamento p", Pagamento.class).getResultList();
        if (pagamentos.isEmpty()) {
            System.out.println("Nenhum pagamento encontrado.");
        } else {
            for (Pagamento p : pagamentos) {
                System.out.println("ID: " + p.getId() + ", Valor: " + p.getValorPago() + ", Forma: " + p.getFormaPagamento() +
                        ", Data: " + p.getDataPagamento() + ", Status: " + p.getStatus());
            }
        }
    }

    private static void deletarPagamento(EntityManager em, Scanner scanner) {
        System.out.println("\n### DELETAR PAGAMENTO ###");

        System.out.print("Digite o ID do pagamento para deletar: ");
        long pagamentoId = scanner.nextLong();
        scanner.nextLine();

        Pagamento pagamento = em.find(Pagamento.class, pagamentoId);
        if (pagamento == null) {
            System.out.println("Pagamento não encontrado.");
            return;
        }

        em.getTransaction().begin();
        em.remove(pagamento);
        em.getTransaction().commit();

        System.out.println("Pagamento excluído com sucesso! ID: " + pagamento.getId());
    }
}
