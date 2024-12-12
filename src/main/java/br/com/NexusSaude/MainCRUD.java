package br.com.NexusSaude;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class MainCRUD {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("nexus-saude");
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

            // === INSERIR ===
            System.out.println("### INSERIR ###");
            
            // Inserir uma especialidade
            Especialidade especialidade = new Especialidade();
            especialidade.setNome("Pediatria");
            em.persist(especialidade);
            System.out.println("Especialidade criada: ID " + especialidade.getId() + ", Nome: " + especialidade.getNome());

            // Inserir um médico
            Usuario medicoUsuario = new Usuario();
            medicoUsuario.setNome("Dr. João Pereira");
            medicoUsuario.setEmail("joao.pereira@exemplo.com");
            medicoUsuario.setSenha("senha123");
            medicoUsuario.setTipoUsuario("medico");
            medicoUsuario.setStatus("ativo");
            em.persist(medicoUsuario);

            Medico medico = new Medico();
            medico.setUsuario(medicoUsuario);
            medico.setCrm("112233");
            medico.setEspecialidade(especialidade);
            medico.setHorariosDisponiveis("Seg-Sex, 8h-12h");
            em.persist(medico);
            System.out.println("Médico criado: ID " + medico.getId() + ", Nome: " + medico.getUsuario().getNome() + ", CRM: " + medico.getCrm());

            // Inserir um paciente
            Usuario pacienteUsuario = new Usuario();
            pacienteUsuario.setNome("Maria Souza");
            pacienteUsuario.setEmail("maria.souza@exemplo.com");
            pacienteUsuario.setSenha("senha123");
            pacienteUsuario.setTipoUsuario("paciente");
            pacienteUsuario.setStatus("ativo");
            em.persist(pacienteUsuario);

            Paciente paciente = new Paciente();
            paciente.setUsuario(pacienteUsuario);
            paciente.setDataRegistro(LocalDate.now());
            em.persist(paciente);
            System.out.println("Paciente criado: ID " + paciente.getId() + ", Nome: " + paciente.getUsuario().getNome());

            // Inserir uma consulta
            Consulta consulta = new Consulta();
            consulta.setEspecialidade(especialidade);
            consulta.setMedico(medico);
            consulta.setPaciente(paciente);
            consulta.setDataConsulta(LocalDateTime.now().plusDays(7));
            consulta.setValor(200.00);
            consulta.setStatus("Agendada");
            em.persist(consulta);
            System.out.println("Consulta criada: ID " + consulta.getId() + ", Médico: " + consulta.getMedico().getUsuario().getNome() + ", Paciente: " + consulta.getPaciente().getUsuario().getNome());

            em.getTransaction().commit();

            // === ATUALIZAR ===
            System.out.println("\n### ATUALIZAR ###");
            em.getTransaction().begin();

            // Atualizar os horários do médico
            Medico medicoParaAtualizar = em.find(Medico.class, medico.getId());
            if (medicoParaAtualizar != null) {
                medicoParaAtualizar.setHorariosDisponiveis("Seg-Sex, 14h-18h");
                em.merge(medicoParaAtualizar);
                System.out.println("Médico atualizado: ID " + medicoParaAtualizar.getId() + ", Novo horário: " + medicoParaAtualizar.getHorariosDisponiveis());
            } else {
                System.out.println("Médico não encontrado para atualização.");
            }

            em.getTransaction().commit();

            // === CONSULTAR ===
            System.out.println("\n### CONSULTAR ###");
            List<Medico> medicos = em.createQuery("SELECT m FROM Medico m", Medico.class).getResultList();
            if (!medicos.isEmpty()) {
                for (Medico m : medicos) {
                    System.out.println("Médico: ID " + m.getId() + ", Nome: " + m.getUsuario().getNome() + ", CRM: " + m.getCrm());
                }
            } else {
                System.out.println("Nenhum médico encontrado.");
            }

            // === DELETAR ===
            System.out.println("\n### DELETAR ###");
            em.getTransaction().begin();

            // Deletar a consulta
            Consulta consultaParaExcluir = em.find(Consulta.class, consulta.getId());
            if (consultaParaExcluir != null) {
                em.remove(consultaParaExcluir);
                System.out.println("Consulta com ID " + consultaParaExcluir.getId() + " excluída.");
            } else {
                System.out.println("Consulta não encontrada para exclusão.");
            }

            em.getTransaction().commit();

            System.out.println("\nOperações de CRUD realizadas com sucesso!");

        } catch (Exception e) {
            em.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            em.close();
            emf.close();
        }
    }
}
