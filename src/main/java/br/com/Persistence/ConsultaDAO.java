package br.com.Persistence;

import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

import br.com.Entidade.Consulta;
import br.com.Entidade.Medico;
import br.com.Entidade.Paciente;

public class ConsultaDAO {
    private EntityManager em;

    public ConsultaDAO(EntityManager em) {
        this.em = em;
    }
    
    public void agendarConsulta(Scanner scanner, MedicoDAO medicoDAO, PacienteDAO pacienteDAO) {
        System.out.println("\n### AGENDAR CONSULTA ###");

        medicoDAO.listar();
        System.out.print("Digite o ID do médico: ");
        long medicoId = scanner.nextLong();
        scanner.nextLine();
        Medico medico = em.find(Medico.class, medicoId);
        if (medico == null) {
            System.out.println("Médico não encontrado. Operação cancelada.");
            return;
        }

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
        LocalDateTime dataConsulta = parseHorario(horarioSelecionado);

        Consulta consulta = new Consulta();
        consulta.setMedico(medico);
        consulta.setPaciente(paciente);
        consulta.setEspecialidade(medico.getEspecialidade());
        consulta.setDataConsulta(dataConsulta);
        consulta.setStatus("Agendada");

        try {
            em.getTransaction().begin();
            em.persist(consulta);
            em.getTransaction().commit();
            System.out.println("Consulta agendada com sucesso! ID da consulta: " + consulta.getId());
        } catch (Exception e) {
            em.getTransaction().rollback();
            System.out.println("Erro ao agendar consulta: " + e.getMessage());
        }
    }

    private LocalDateTime parseHorario(String horarioSelecionado) {
    	String[] partes = horarioSelecionado.split(":");
        String horario = partes[1].trim();
        String[] intervalo = horario.split("-");
        String horarioInicio = intervalo[0].trim();

        return LocalDateTime.now()
            .withHour(Integer.parseInt(horarioInicio.split(":")[0]))
            .withMinute(0)
            .withSecond(0)
            .withNano(0);
    }


    public void cadastrar(Scanner scanner) {
        System.out.println("\n### CADASTRAR CONSULTA ###");

        System.out.print("Digite o ID do médico: ");
        long medicoId = scanner.nextLong();
        scanner.nextLine();

        Medico medico = em.find(Medico.class, medicoId);
        if (medico == null) {
            System.out.println("Médico não encontrado! Cadastre um médico antes.");
            return;
        }

        System.out.print("Digite o ID do paciente: ");
        long pacienteId = scanner.nextLong();
        scanner.nextLine();

        Paciente paciente = em.find(Paciente.class, pacienteId);
        if (paciente == null) {
            System.out.println("Paciente não encontrado! Cadastre um paciente antes.");
            return;
        }

        System.out.print("Digite a data e hora da consulta (formato: YYYY-MM-DDTHH:MM): ");
        String dataHora = scanner.nextLine();
        LocalDateTime dataConsulta;
        try {
            dataConsulta = LocalDateTime.parse(dataHora);
        } catch (Exception e) {
            System.out.println("Data e hora inválidas! Tente novamente.");
            return;
        }

        em.getTransaction().begin();

        Consulta consulta = new Consulta();
        consulta.setMedico(medico);
        consulta.setPaciente(paciente);
        consulta.setEspecialidade(medico.getEspecialidade());
        consulta.setDataConsulta(dataConsulta);
        consulta.setStatus("Agendada");
        em.persist(consulta);

        em.getTransaction().commit();
        System.out.println("Consulta cadastrada com sucesso! ID: " + consulta.getId());
    }

    public List<Consulta> listar() {
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
                        ", Status: " + consulta.getStatus());
            }
        }
        return consultas;
    }

    public void salvar(Consulta consulta) {
        em.persist(consulta);
    }


    public void atualizar(Scanner scanner) {
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
        } else {
            System.out.println("\nHorários disponíveis do médico:");
            for (int i = 0; i < horariosDisponiveis.size(); i++) {
                System.out.println((i + 1) + ". " + horariosDisponiveis.get(i));
            }

            System.out.print("Escolha um novo horário (digite o número correspondente ou pressione Enter para não alterar): ");
            String escolhaHorario = scanner.nextLine();

            if (!escolhaHorario.isBlank()) {
                try {
                    int escolha = Integer.parseInt(escolhaHorario);
                    if (escolha < 1 || escolha > horariosDisponiveis.size()) {
                        System.out.println("Opção inválida! Mantendo o horário atual.");
                    } else {
                        String horarioSelecionado = horariosDisponiveis.get(escolha - 1);
                        LocalDateTime novoHorario = parseHorario(horarioSelecionado);
                        consulta.setDataConsulta(novoHorario);
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Entrada inválida! Mantendo o horário atual.");
                }
            }
        }

        System.out.print("Digite o novo status da consulta (Agendada/Concluída/Paga ou pressione Enter para não alterar): ");
        String novoStatus = scanner.nextLine();
        if (!novoStatus.isBlank()) {
            consulta.setStatus(novoStatus);
        }

        try {
            em.getTransaction().begin();
            em.merge(consulta);
            em.getTransaction().commit();
            System.out.println("Consulta atualizada com sucesso! ID: " + consulta.getId());
        } catch (Exception e) {
            em.getTransaction().rollback();
            System.out.println("Erro ao atualizar consulta: " + e.getMessage());
        }
    }

    
    public Consulta consultarPorId(Long id) {
        Consulta consulta = em.find(Consulta.class, id);
        if (consulta == null) {
            System.out.println("Consulta com ID " + id + " não encontrada.");
        } else {
            System.out.println("Consulta encontrada: ID: " + consulta.getId() +
                    ", Médico: " + consulta.getMedico().getUsuario().getNome() +
                    ", Paciente: " + consulta.getPaciente().getUsuario().getNome() +
                    ", Data: " + consulta.getDataConsulta() +
                    ", Status: " + consulta.getStatus());
        }
        return consulta;
    }

    public void remover(Scanner scanner) {
        System.out.println("\n### REMOVER CONSULTA ###");
        System.out.print("Digite o ID da consulta que deseja remover: ");
        long consultaId = scanner.nextLong();
        scanner.nextLine();

        Consulta consulta = em.find(Consulta.class, consultaId);
        if (consulta == null) {
            System.out.println("Consulta não encontrada.");
            return;
        }

        try {
            em.getTransaction().begin();
            em.remove(consulta);
            em.getTransaction().commit();
            System.out.println("Consulta removida com sucesso! ID: " + consultaId);
        } catch (Exception e) {
            em.getTransaction().rollback();
            System.out.println("Erro ao remover consulta: " + e.getMessage());
        }
    }
}
