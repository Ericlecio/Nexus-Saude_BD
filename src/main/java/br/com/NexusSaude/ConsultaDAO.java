package br.com.NexusSaude;

import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

public class ConsultaDAO {
    private EntityManager em;

    public ConsultaDAO(EntityManager em) {
        this.em = em;
    }

    // Método para cadastrar uma nova consulta
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

    // Método para listar todas as consultas
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

    // Método para salvar uma consulta
    public void salvar(Consulta consulta) {
        em.persist(consulta);
    }

    // Método para atualizar uma consulta
    public void atualizar(Consulta consulta) {
        em.getTransaction().begin();
        em.merge(consulta);
        em.getTransaction().commit();
        System.out.println("Consulta atualizada com sucesso! ID: " + consulta.getId());
    }

    // Método para consultar uma consulta específica
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

    // Método para remover uma consulta
    public void remover(Long id) {
        em.getTransaction().begin();
        Consulta consulta = em.find(Consulta.class, id);
        if (consulta != null) {
            em.remove(consulta);
            em.getTransaction().commit();
            System.out.println("Consulta removida com sucesso! ID: " + id);
        } else {
            em.getTransaction().rollback();
            System.out.println("Consulta com ID " + id + " não encontrada. Operação cancelada.");
        }
    }
}
