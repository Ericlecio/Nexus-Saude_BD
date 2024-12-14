package br.com.NexusSaude;

import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class Main2 {

    // Método principal
    public static void main(String[] args) {
        EntityManager em = JPAUtil.getEntityManager(); // Obtenha o EntityManager da classe JPAUtil
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

    // Menu principal do agendamento
    public static void menuAgendamento(EntityManager em, Scanner scanner) {
        boolean continuar = true;

        while (continuar) {
            System.out.println("\n### MENU AGENDAMENTO ###");
            System.out.println("1. Agendar Consulta");
            System.out.println("2. Listar Consultas");
            System.out.println("3. Realizar Pagamento");
            System.out.println("4. Listar Pagamentos");
            System.out.println("5. Voltar");
            System.out.print("Escolha uma opção: ");

            try {
                int opcao = scanner.nextInt();
                scanner.nextLine(); // Limpar buffer

                switch (opcao) {
                    case 1:
                        agendarConsulta(em, scanner);
                        break;
                    case 2:
                        listarConsultas(em);
                        break;
                    case 3:
                        realizarPagamento(em, scanner);
                        break;
                    case 4:
                        listarPagamentos(em);
                        break;
                    case 5:
                        continuar = false;
                        break;
                    default:
                        System.out.println("Opção inválida! Tente novamente.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Entrada inválida! Por favor, tente novamente.");
                scanner.nextLine(); // Limpar buffer após erro
            }
        }
    }

    // Método para agendar consulta
    private static void agendarConsulta(EntityManager em, Scanner scanner) {
        System.out.println("\n### AGENDAR CONSULTA ###");

        // Listar médicos disponíveis
        List<Medico> medicos = em.createQuery("SELECT m FROM Medico m", Medico.class).getResultList();
        if (medicos.isEmpty()) {
            System.out.println("Nenhum médico cadastrado. Cadastre médicos antes de agendar consultas.");
            return;
        }
        System.out.println("Médicos disponíveis:");
        for (Medico medico : medicos) {
            System.out.println("ID: " + medico.getId() + ", Nome: " + medico.getUsuario().getNome() +
                    ", Especialidade: " + medico.getEspecialidade().getNome() +
                    ", Horários Disponíveis: " + medico.getHorariosDisponiveis());
        }

        Medico medicoSelecionado = null;
        while (medicoSelecionado == null) {
            try {
                System.out.print("Digite o ID do médico: ");
                long medicoId = scanner.nextLong();
                scanner.nextLine();
                medicoSelecionado = em.find(Medico.class, medicoId);
                if (medicoSelecionado == null) {
                    System.out.println("Médico não encontrado. Tente novamente.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Entrada inválida! Insira um número válido.");
                scanner.nextLine();
            }
        }

        // Listar pacientes disponíveis
        List<Paciente> pacientes = em.createQuery("SELECT p FROM Paciente p", Paciente.class).getResultList();
        if (pacientes.isEmpty()) {
            System.out.println("Nenhum paciente cadastrado. Cadastre pacientes antes de agendar consultas.");
            return;
        }
        System.out.println("Pacientes cadastrados:");
        for (Paciente paciente : pacientes) {
            System.out.println("ID: " + paciente.getId() + ", Nome: " + paciente.getUsuario().getNome());
        }

        Paciente pacienteSelecionado = null;
        while (pacienteSelecionado == null) {
            try {
                System.out.print("Digite o ID do paciente: ");
                long pacienteId = scanner.nextLong();
                scanner.nextLine();
                pacienteSelecionado = em.find(Paciente.class, pacienteId);
                if (pacienteSelecionado == null) {
                    System.out.println("Paciente não encontrado. Tente novamente.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Entrada inválida! Insira um número válido.");
                scanner.nextLine();
            }
        }

        // Solicitar data da consulta
        System.out.print("Digite a data e hora da consulta (formato: YYYY-MM-DDTHH:MM): ");
        String dataHora = scanner.nextLine();
        LocalDateTime dataConsulta;
        try {
            dataConsulta = LocalDateTime.parse(dataHora);
        } catch (Exception e) {
            System.out.println("Data e hora inválidas. Tente novamente.");
            return;
        }

        System.out.print("Digite o valor da consulta: ");
        double valorConsulta;
        try {
            valorConsulta = scanner.nextDouble();
            scanner.nextLine();
        } catch (InputMismatchException e) {
            System.out.println("Valor inválido. Tente novamente.");
            scanner.nextLine();
            return;
        }

        // Criar consulta
        em.getTransaction().begin();
        Consulta consulta = new Consulta();
        consulta.setMedico(medicoSelecionado);
        consulta.setPaciente(pacienteSelecionado);
        consulta.setEspecialidade(medicoSelecionado.getEspecialidade());
        consulta.setDataConsulta(dataConsulta);
        consulta.setValor(valorConsulta);
        consulta.setStatus("Agendada");
        em.persist(consulta);
        em.getTransaction().commit();

        System.out.println("Consulta agendada com sucesso! ID: " + consulta.getId() +
                ", Médico: " + medicoSelecionado.getUsuario().getNome() +
                ", Paciente: " + pacienteSelecionado.getUsuario().getNome() +
                ", Data: " + consulta.getDataConsulta());
    }

    // Listar consultas
    private static void listarConsultas(EntityManager em) {
        System.out.println("\n### LISTAR CONSULTAS ###");

        List<Consulta> consultas = em.createQuery("SELECT c FROM Consulta c", Consulta.class).getResultList();
        if (consultas.isEmpty()) {
            System.out.println("Nenhuma consulta encontrada.");
        } else {
            for (Consulta consulta : consultas) {
                System.out.println("ID: " + consulta.getId() +
                        ", Médico: " + consulta.getMedico().getUsuario().getNome() +
                        ", Paciente: " + consulta.getPaciente().getUsuario().getNome() +
                        ", Data: " + consulta.getDataConsulta() +
                        ", Valor: " + consulta.getValor() +
                        ", Status: " + consulta.getStatus());
            }
        }
    }

    // Realizar pagamento
    private static void realizarPagamento(EntityManager em, Scanner scanner) {
        System.out.println("\n### REALIZAR PAGAMENTO ###");

        listarConsultas(em);

        System.out.print("Digite o ID da consulta para realizar o pagamento: ");
        long consultaId = scanner.nextLong();
        scanner.nextLine();

        Consulta consulta = em.find(Consulta.class, consultaId);
        if (consulta == null) {
            System.out.println("Consulta não encontrada.");
            return;
        }

        em.getTransaction().begin();
        Pagamento pagamento = new Pagamento();
        pagamento.setConsulta(consulta);
        pagamento.setValorPago(consulta.getValor());
        pagamento.setFormaPagamento("Cartão"); // Forma de pagamento padrão
        pagamento.setStatus("Pago");
        em.persist(pagamento);
        em.getTransaction().commit();

        System.out.println("Pagamento realizado com sucesso! ID do Pagamento: " + pagamento.getId());
    }

    // Listar pagamentos
    private static void listarPagamentos(EntityManager em) {
        System.out.println("\n### LISTAR PAGAMENTOS ###");

        List<Pagamento> pagamentos = em.createQuery("SELECT p FROM Pagamento p", Pagamento.class).getResultList();
        if (pagamentos.isEmpty()) {
            System.out.println("Nenhum pagamento encontrado.");
        } else {
            for (Pagamento pagamento : pagamentos) {
                System.out.println("ID: " + pagamento.getId() +
                        ", Consulta: " + pagamento.getConsulta().getId() +
                        ", Valor Pago: " + pagamento.getValorPago() +
                        ", Status: " + pagamento.getStatus());
            }
        }
    }
}
