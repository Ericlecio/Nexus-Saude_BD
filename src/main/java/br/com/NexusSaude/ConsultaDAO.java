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
        em.persist(consulta); // Persistir a consulta no banco
    }


	public void atualizar(Consulta consulta) {
		// TODO Auto-generated method stub
		
	}
}
