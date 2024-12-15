package br.com.NexusSaude;

import jakarta.persistence.EntityManager;

import java.time.LocalDateTime;
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
                        agendarConsulta(em, consultaDAO, medicoDAO, pacienteDAO, scanner);
                        break;
                    case 2:
                        consultaDAO.listar();
                        break;
                    case 3:
                        atualizarConsulta(em, consultaDAO, scanner);
                        break;
                    case 4:
                        removerConsulta(em, consultaDAO, scanner);
                        break;
                    case 5:
                        realizarPagamento(em, consultaDAO, pagamentoDAO, scanner);
                        break;
                    case 6:
                        pagamentoDAO.listar();
                        break;
                    case 7:
                        atualizarPagamento(em, pagamentoDAO, scanner);
                        break;
                    case 8:
                        removerPagamento(em, pagamentoDAO, scanner);
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

    private static void agendarConsulta(EntityManager em, ConsultaDAO consultaDAO, MedicoDAO medicoDAO, PacienteDAO pacienteDAO, Scanner scanner) {
        System.out.println("\n### AGENDAR CONSULTA ###");

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

        List<String> horariosDisponiveis = medico.getDiasAtendimento();
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

        String horarioSelecionado = horariosDisponiveis.get(escolhaHorario - 1);

        String[] partes = horarioSelecionado.split(":");
        String dia = partes[0].trim();
        String horario = partes[1].trim();

        String[] intervalo = horario.split("-");
        String horarioInicio = intervalo[0].trim();

        java.time.LocalDateTime dataConsulta = java.time.LocalDateTime.now()
            .withHour(Integer.parseInt(horarioInicio.split(":")[0]))
            .withMinute(0)
            .withSecond(0)
            .withNano(0);

        Consulta consulta = new Consulta();
        consulta.setMedico(medico);
        consulta.setPaciente(paciente);
        consulta.setEspecialidade(medico.getEspecialidade());
        consulta.setDataConsulta(dataConsulta);
        consulta.setStatus("Agendada");

        em.getTransaction().begin();
        consultaDAO.salvar(consulta);
        em.getTransaction().commit();

        System.out.println("Consulta agendada com sucesso! ID da consulta: " + consulta.getId() +
            ", Data e Hora: " + consulta.getDataConsulta());
    }


    private static void realizarPagamento(EntityManager em, ConsultaDAO consultaDAO, PagamentoDAO pagamentoDAO, Scanner scanner) {
        System.out.println("\n### REALIZAR PAGAMENTO ###");

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

        Consulta consulta = em.find(Consulta.class, consultaId);
        if (consulta == null || !"Agendada".equals(consulta.getStatus())) {
            System.out.println("Consulta inválida ou já paga. Operação cancelada.");
            return;
        }

        System.out.print("Digite o valor da consulta: ");
        double valorConsulta = scanner.nextDouble();
        scanner.nextLine();

        System.out.print("Digite a forma de pagamento (Cartão, Dinheiro ou PIX.): ");
        String formaPagamento = scanner.nextLine();

        try {
            em.getTransaction().begin();

            Pagamento pagamento = new Pagamento();
            pagamento.setConsulta(consulta);
            pagamento.setValorPago(valorConsulta);
            pagamento.setFormaPagamento(formaPagamento);
            pagamento.setStatus("Pago");
            pagamento.setDataPagamento(new java.sql.Date(System.currentTimeMillis()));

            em.persist(pagamento);

            consulta.setStatus("Paga");
            em.merge(consulta);

            em.getTransaction().commit();

            System.out.println("Pagamento realizado com sucesso! ID do Pagamento: " + pagamento.getId());
        } catch (Exception e) {
            em.getTransaction().rollback();
            System.out.println("Erro ao realizar o pagamento: " + e.getMessage());
        }
    }


    private static void atualizarConsulta(EntityManager em, ConsultaDAO consultaDAO, Scanner scanner) {
        System.out.println("\n### ATUALIZAR CONSULTA ###");
        System.out.print("Digite o ID da consulta que deseja atualizar: ");
        long consultaId = scanner.nextLong();
        scanner.nextLine();

        Consulta consulta = em.find(Consulta.class, consultaId);
        if (consulta == null) {
            System.out.println("Consulta não encontrada.");
            return;
        }

        Medico medico = consulta.getMedico();
        if (medico == null) {
            System.out.println("Erro: Médico associado à consulta não encontrado.");
            return;
        }

        List<String> horariosDisponiveis = medico.getDiasAtendimento();
        if (horariosDisponiveis == null || horariosDisponiveis.isEmpty()) {
            System.out.println("O médico não possui horários disponíveis.");
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

        String horarioSelecionado = horariosDisponiveis.get(escolhaHorario - 1);
        String[] partes = horarioSelecionado.split(":");
        String dia = partes[0].trim();
        String horario = partes[1].trim();

        String[] intervalo = horario.split("-");
        String horarioInicio = intervalo[0].trim();

        try {
            LocalDateTime novaData = LocalDateTime.now()
                .withHour(Integer.parseInt(horarioInicio.split(":")[0]))
                .withMinute(0)
                .withSecond(0)
                .withNano(0);
            consulta.setDataConsulta(novaData);

            System.out.print("Digite o novo status da consulta (Agendada/Concluída/Paga): ");
            String novoStatus = scanner.nextLine();
            consulta.setStatus(novoStatus);

            consultaDAO.atualizar(consulta);
            System.out.println("Consulta atualizada com sucesso! ID: " + consulta.getId());
        } catch (Exception e) {
            System.out.println("Erro ao atualizar consulta: " + e.getMessage());
        }
    }


    private static void removerConsulta(EntityManager em, ConsultaDAO consultaDAO, Scanner scanner) {
        System.out.println("\n### REMOVER CONSULTA ###");
        System.out.print("Digite o ID da consulta que deseja remover: ");
        long consultaId = scanner.nextLong();
        scanner.nextLine();

        Consulta consulta = em.find(Consulta.class, consultaId);
        if (consulta == null) {
            System.out.println("Consulta não encontrada.");
            return;
        }

        if ("Paga".equals(consulta.getStatus())) {
            System.out.println("Não é possível remover uma consulta já paga. Cancele o pagamento primeiro.");
            return;
        }

        consultaDAO.remover(consultaId);
        System.out.println("Consulta removida com sucesso!");
    }


    private static void atualizarPagamento(EntityManager em, PagamentoDAO pagamentoDAO, Scanner scanner) {
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

        pagamentoDAO.atualizar(pagamento);
        System.out.println("Pagamento atualizado com sucesso! ID: " + pagamento.getId());
    }

    private static void removerPagamento(EntityManager em, PagamentoDAO pagamentoDAO, Scanner scanner) {
        System.out.println("\n### REMOVER PAGAMENTO ###");
        System.out.print("Digite o ID do pagamento que deseja remover: ");
        long pagamentoId = scanner.nextLong();
        scanner.nextLine();

        Pagamento pagamento = em.find(Pagamento.class, pagamentoId);
        if (pagamento == null) {
            System.out.println("Pagamento não encontrado.");
            return;
        }

        Consulta consulta = pagamento.getConsulta();
        if (consulta != null) {
            try {
                em.getTransaction().begin();
                consulta.setStatus("Agendada");
                em.merge(consulta);
                em.getTransaction().commit();
                System.out.println("Status da consulta atualizado para 'Agendada'.");
            } catch (Exception e) {
                em.getTransaction().rollback();
                System.out.println("Erro ao atualizar status da consulta: " + e.getMessage());
                return;
            }
        }

        try {
            pagamentoDAO.remover(pagamentoId);
            System.out.println("Pagamento removido com sucesso!");
        } catch (Exception e) {
            System.out.println("Erro ao remover pagamento: " + e.getMessage());
        }
    }
}