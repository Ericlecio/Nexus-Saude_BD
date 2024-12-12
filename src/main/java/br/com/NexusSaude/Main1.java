package br.com.NexusSaude;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class Main1 {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("nexus-saude");
        EntityManager em = emf.createEntityManager();

        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("### INICIANDO OPERAÇÕES ###");

            // === INSERIR ===
            System.out.println("\n### INSERIR ###");
            em.getTransaction().begin();

            System.out.println("Digite o nome da especialidade:");
            String nomeEspecialidade = scanner.nextLine();
            Especialidade especialidade = new Especialidade();
            especialidade.setNome(nomeEspecialidade);
            em.persist(especialidade);
            System.out.println("Especialidade criada: ID " + especialidade.getId() + ", Nome: " + especialidade.getNome());

            System.out.println("Digite o nome do médico:");
            String nomeMedico = scanner.nextLine();
            System.out.println("Digite o email do médico:");
            String emailMedico = scanner.nextLine();
            System.out.println("Digite o CRM do médico:");
            String crmMedico = scanner.nextLine();
            System.out.println("Digite os horários disponíveis do médico:");
            String horariosMedico = scanner.nextLine();

            Usuario medicoUsuario = new Usuario();
            medicoUsuario.setNome(nomeMedico);
            medicoUsuario.setEmail(emailMedico);
            medicoUsuario.setSenha("senha123");
            medicoUsuario.setTipoUsuario("medico");
            medicoUsuario.setStatus("ativo");
            em.persist(medicoUsuario);

            Medico medico = new Medico();
            medico.setUsuario(medicoUsuario);
            medico.setCrm(crmMedico);
            medico.setEspecialidade(especialidade);
            medico.setHorariosDisponiveis(horariosMedico);
            em.persist(medico);
            System.out.println("Médico criado: ID " + medico.getId() + ", Nome: " + medicoUsuario.getNome() + ", CRM: " + medico.getCrm());

            System.out.println("Digite o nome do paciente:");
            String nomePaciente = scanner.nextLine();
            System.out.println("Digite o email do paciente:");
            String emailPaciente = scanner.nextLine();

            Usuario pacienteUsuario = new Usuario();
            pacienteUsuario.setNome(nomePaciente);
            pacienteUsuario.setEmail(emailPaciente);
            pacienteUsuario.setSenha("senha123");
            pacienteUsuario.setTipoUsuario("paciente");
            pacienteUsuario.setStatus("ativo");
            em.persist(pacienteUsuario);

            Paciente paciente = new Paciente();
            paciente.setUsuario(pacienteUsuario);
            paciente.setDataRegistro(LocalDate.now());
            em.persist(paciente);
            System.out.println("Paciente criado: ID " + paciente.getId() + ", Nome: " + pacienteUsuario.getNome());

            System.out.println("Digite o valor da consulta:");
            double valorConsulta = scanner.nextDouble();
            scanner.nextLine(); // Limpar o buffer

            Consulta consulta = new Consulta();
            consulta.setEspecialidade(especialidade);
            consulta.setMedico(medico);
            consulta.setPaciente(paciente);
            consulta.setDataConsulta(LocalDateTime.now().plusDays(7));
            consulta.setValor(valorConsulta);
            consulta.setStatus("Agendada");
            em.persist(consulta);
            System.out.println("Consulta criada: ID " + consulta.getId() + ", Médico: " + medicoUsuario.getNome() + ", Paciente: " + pacienteUsuario.getNome());

            em.getTransaction().commit();
            System.out.println("Inserção concluída com sucesso.");

            // === ATUALIZAR ===
            System.out.println("\n### ATUALIZAR ###");
            em.getTransaction().begin();

            boolean idValido = false;
            long medicoId = 0;

            // Solicitar o ID do médico até que seja um número válido
            while (!idValido) {
                try {
                    System.out.println("Digite o ID do médico para atualizar os horários:");
                    medicoId = scanner.nextLong();
                    scanner.nextLine(); // Limpar o buffer
                    idValido = true;
                } catch (InputMismatchException e) {
                    System.out.println("Entrada inválida! Por favor, insira um número.");
                    scanner.nextLine(); // Limpar o buffer
                }
            }

            Medico medicoParaAtualizar = em.find(Medico.class, medicoId);
            if (medicoParaAtualizar != null) {
                System.out.println("Médico encontrado: ID " + medicoParaAtualizar.getId() + ", Horários: " + medicoParaAtualizar.getHorariosDisponiveis());
                System.out.println("Digite os novos horários do médico:");
                String novosHorarios = scanner.nextLine();
                medicoParaAtualizar.setHorariosDisponiveis(novosHorarios);
                em.merge(medicoParaAtualizar);
                System.out.println("Médico atualizado: Novo horário: " + medicoParaAtualizar.getHorariosDisponiveis());
            } else {
                System.out.println("Médico não encontrado para atualização.");
            }

            em.getTransaction().commit();
            System.out.println("Atualização concluída com sucesso.");

            // === CONSULTAR ===
            System.out.println("\n### CONSULTAR ###");
            System.out.println("Buscando todos os médicos...");
            List<Medico> medicos = em.createQuery("SELECT m FROM Medico m", Medico.class).getResultList();
            if (!medicos.isEmpty()) {
                for (Medico m : medicos) {
                    System.out.println("Médico: ID " + m.getId() + ", Nome: " + m.getUsuario().getNome() + ", CRM: " + m.getCrm() + ", Horários: " + m.getHorariosDisponiveis());
                }
            } else {
                System.out.println("Nenhum médico encontrado.");
            }

            // === DELETAR ===
            System.out.println("\n### DELETAR ###");
            em.getTransaction().begin();

            boolean consultaIdValido = false;
            long consultaId = 0;

            // Solicitar o ID da consulta até que seja um número válido
            while (!consultaIdValido) {
                try {
                    System.out.println("Digite o ID da consulta para exclusão:");
                    consultaId = scanner.nextLong();
                    scanner.nextLine(); // Limpar o buffer
                    consultaIdValido = true;
                } catch (InputMismatchException e) {
                    System.out.println("Entrada inválida! Por favor, insira um número.");
                    scanner.nextLine(); // Limpar o buffer
                }
            }

            Consulta consultaParaExcluir = em.find(Consulta.class, consultaId);
            if (consultaParaExcluir != null) {
                System.out.println("Consulta encontrada: ID " + consultaParaExcluir.getId() + ", Médico: " + consultaParaExcluir.getMedico().getUsuario().getNome());
                em.remove(consultaParaExcluir);
                System.out.println("Consulta com ID " + consultaParaExcluir.getId() + " excluída.");
            } else {
                System.out.println("Consulta não encontrada para exclusão.");
            }

            em.getTransaction().commit();
            System.out.println("Exclusão concluída com sucesso.");

            System.out.println("\n### TODAS AS OPERAÇÕES FORAM REALIZADAS COM SUCESSO ###");

        } catch (Exception e) {
            em.getTransaction().rollback();
            e.printStackTrace();
            System.out.println("Ocorreu um erro durante as operações. Alterações revertidas.");
        } finally {
            em.close();
            emf.close();
            System.out.println("\nConexão encerrada.");
        }
    }
}
