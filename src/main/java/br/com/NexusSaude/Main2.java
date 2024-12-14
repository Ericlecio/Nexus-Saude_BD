package br.com.NexusSaude;

import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Scanner;

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
            System.out.println("3. Realizar Pagamento");
            System.out.println("4. Listar Pagamentos");
            System.out.println("5. Voltar");
            System.out.print("Escolha uma opção: ");

            try {
                int opcao = scanner.nextInt();
                scanner.nextLine(); // Limpar buffer

                switch (opcao) {
                    case 1:
                        agendarConsulta(em, consultaDAO, medicoDAO, pacienteDAO, scanner);
                        break;
                    case 2:
                        consultaDAO.listar();
                        break;
                    case 3:
                        realizarPagamento(em, consultaDAO, pagamentoDAO, scanner);
                        break;
                    case 4:
                        pagamentoDAO.listar();
                        break;
                    case 5:
                        continuar = false;
                        break;
                    default:
                        System.out.println("Opção inválida! Tente novamente.");
                }
            } catch (Exception e) {
                System.out.println("Entrada inválida! Por favor, tente novamente.");
                scanner.nextLine(); // Limpar buffer após erro
            }
        }
    }

    private static void agendarConsulta(EntityManager em, ConsultaDAO consultaDAO, MedicoDAO medicoDAO, PacienteDAO pacienteDAO, Scanner scanner) {
        System.out.println("\n### AGENDAR CONSULTA ###");

        // Listar médicos disponíveis
        System.out.println("\nMédicos disponíveis:");
        medicoDAO.listar();
        System.out.print("Digite o ID do médico: ");
        long medicoId = scanner.nextLong();
        scanner.nextLine();

        Medico medico = em.find(Medico.class, medicoId);
        if (medico == null) {
            System.out.println("Médico não encontrado. Operação cancelada.");
            return;
        }

        // Listar pacientes cadastrados
        System.out.println("\nPacientes cadastrados:");
        pacienteDAO.listar();
        System.out.print("Digite o ID do paciente: ");
        long pacienteId = scanner.nextLong();
        scanner.nextLine();

        Paciente paciente = em.find(Paciente.class, pacienteId);
        if (paciente == null) {
            System.out.println("Paciente não encontrado. Operação cancelada.");
            return;
        }

        // Listar horários disponíveis do médico
        List<String> horariosDisponiveis = medico.getDiasAtendimento(); // Horários configurados no cadastro
        if (horariosDisponiveis == null || horariosDisponiveis.isEmpty()) {
            System.out.println("O médico não possui horários disponíveis. Operação cancelada.");
            return;
        }

        System.out.println("\nHorários disponíveis do médico:");
        for (int i = 0; i < horariosDisponiveis.size(); i++) {
            System.out.println((i + 1) + ". " + horariosDisponiveis.get(i));
        }

        System.out.print("Escolha um horário (digite o número correspondente): ");
        int escolhaHorario = scanner.nextInt();
        scanner.nextLine();

        if (escolhaHorario < 1 || escolhaHorario > horariosDisponiveis.size()) {
            System.out.println("Opção inválida. Operação cancelada.");
            return;
        }

        // Processar o horário escolhido
        String horarioSelecionado = horariosDisponiveis.get(escolhaHorario - 1);

        // Separar "Segunda-feira: 09 - 12" em partes
        String[] partes = horarioSelecionado.split(":");
        String dia = partes[0].trim();
        String horario = partes[1].trim();

        // Separar o intervalo de horas (ex: "09 - 12")
        String[] intervalo = horario.split("-");
        String horarioInicio = intervalo[0].trim();

        // Criar a data e hora da consulta
        java.time.LocalDateTime dataConsulta = java.time.LocalDateTime.now()
            .withHour(Integer.parseInt(horarioInicio.split(":")[0]))
            .withMinute(0)
            .withSecond(0)
            .withNano(0);

        // Criar a consulta
        Consulta consulta = new Consulta();
        consulta.setMedico(medico);
        consulta.setPaciente(paciente);
        consulta.setEspecialidade(medico.getEspecialidade());
        consulta.setDataConsulta(dataConsulta);
        consulta.setStatus("Agendada");

        // Persistir a consulta no banco de dados
        em.getTransaction().begin(); // Iniciar transação
        consultaDAO.salvar(consulta);
        em.getTransaction().commit(); // Confirmar transação

        System.out.println("Consulta agendada com sucesso! ID da consulta: " + consulta.getId() +
            ", Data e Hora: " + consulta.getDataConsulta());
    }



    private static void realizarPagamento(EntityManager em, ConsultaDAO consultaDAO, PagamentoDAO pagamentoDAO, Scanner scanner) {
        System.out.println("\n### REALIZAR PAGAMENTO ###");

        // Listar consultas pendentes
        List<Consulta> consultas = consultaDAO.listar();
        if (consultas.isEmpty()) {
            System.out.println("Nenhuma consulta encontrada para pagamento.");
            return;
        }

        System.out.println("Consultas disponíveis para pagamento:");
        for (Consulta consulta : consultas) {
            if ("Agendada".equals(consulta.getStatus())) {
                System.out.println("ID: " + consulta.getId() + ", Paciente: " + consulta.getPaciente().getUsuario().getNome() +
                        ", Médico: " + consulta.getMedico().getUsuario().getNome() +
                        ", Data: " + consulta.getDataConsulta());
            }
        }

        System.out.print("Digite o ID da consulta para realizar o pagamento: ");
        long consultaId = scanner.nextLong();
        scanner.nextLine();

        // Recuperar a consulta
        Consulta consulta = em.find(Consulta.class, consultaId);
        if (consulta == null || !consulta.getStatus().equals("Agendada")) {
            System.out.println("Consulta inválida ou já paga. Operação cancelada.");
            return;
        }

        // Solicitar valor do pagamento
        System.out.print("Digite o valor da consulta: ");
        double valorConsulta = scanner.nextDouble();
        scanner.nextLine();

        // Solicitar forma de pagamento
        System.out.print("Digite a forma de pagamento (Cartão, Dinheiro, etc.): ");
        String formaPagamento = scanner.nextLine();

        // Realizar pagamento e atualizar status
        try {
            em.getTransaction().begin();

            Pagamento pagamento = new Pagamento();
            pagamento.setConsulta(consulta);
            pagamento.setValorPago(valorConsulta);
            pagamento.setFormaPagamento(formaPagamento);
            pagamento.setStatus("Pago");
            pagamento.setDataPagamento(new java.sql.Date(System.currentTimeMillis()));

            em.persist(pagamento);

            // Atualizar o status da consulta para "Paga"
            consulta.setStatus("Paga");
            em.merge(consulta);

            em.getTransaction().commit();

            System.out.println("Pagamento realizado com sucesso! ID do Pagamento: " + pagamento.getId());
        } catch (Exception e) {
            em.getTransaction().rollback();
            System.out.println("Erro ao realizar o pagamento: " + e.getMessage());
        }
    }
}
