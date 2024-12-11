package br.com.NexusSaude;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class MainTeste {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("nexus-saude");
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

            // Criar uma especialidade para testes
            Especialidade especialidade = new Especialidade();
            especialidade.setNome("Dermatologia");
            em.persist(especialidade);
            System.out.println("Especialidade criada:");
            System.out.println("ID: " + especialidade.getId() + ", Nome: " + especialidade.getNome());

            // Criar um usuário associado ao médico
            Usuario medicoUsuario = new Usuario();
            medicoUsuario.setNome("Dra. Ana Costa");
            medicoUsuario.setEmail("ana.costa@exemplo.com");
            medicoUsuario.setSenha("segura123");
            medicoUsuario.setTipoUsuario("medico");
            medicoUsuario.setStatus("ativo");
            em.persist(medicoUsuario);
            System.out.println("Usuário do médico criado:");
            System.out.println("ID: " + medicoUsuario.getId() + ", Nome: " + medicoUsuario.getNome() + ", Email: " + medicoUsuario.getEmail());

            // Criar um médico
            Medico medico = new Medico();
            medico.setUsuario(medicoUsuario);
            medico.setCrm("654321");
            medico.setEspecialidade(especialidade);
            medico.setHorariosDisponiveis("Segunda a Sexta - 08:00 às 17:00");
            em.persist(medico);
            System.out.println("Médico criado:");
            System.out.println("ID: " + medico.getId() + ", CRM: " + medico.getCrm() + ", Especialidade: " + medico.getEspecialidade().getNome());

            // Criar um paciente
            Usuario pacienteUsuario = new Usuario();
            pacienteUsuario.setNome("João da Silva");
            pacienteUsuario.setEmail("joao.silva@exemplo.com");
            pacienteUsuario.setSenha("senha123");
            pacienteUsuario.setTipoUsuario("paciente");
            pacienteUsuario.setStatus("ativo");
            em.persist(pacienteUsuario);
            System.out.println("Usuário do paciente criado:");
            System.out.println("ID: " + pacienteUsuario.getId() + ", Nome: " + pacienteUsuario.getNome() + ", Email: " + pacienteUsuario.getEmail());

            Paciente paciente = new Paciente();
            paciente.setUsuario(pacienteUsuario);
            paciente.setDataRegistro(LocalDate.now());
            em.persist(paciente);
            System.out.println("Paciente criado:");
            System.out.println("ID: " + paciente.getId() + ", Data de Registro: " + paciente.getDataRegistro());

            // Criar uma consulta associada ao médico e ao paciente
            Consulta consulta = new Consulta();
            consulta.setEspecialidade(especialidade);
            consulta.setMedico(medico);
            consulta.setPaciente(paciente); // Associar o paciente à consulta
            consulta.setStatus("Agendada");
            consulta.setValor(150.00);
            consulta.setDataConsulta(LocalDateTime.now().plusDays(5));
            em.persist(consulta);
            System.out.println("Consulta criada:");
            System.out.println("ID: " + consulta.getId() + ", Médico: " + consulta.getMedico().getUsuario().getNome() +
                    ", Paciente: " + consulta.getPaciente().getUsuario().getNome() + 
                    ", Data: " + consulta.getDataConsulta() + ", Valor: " + consulta.getValor());

            em.getTransaction().commit();

            System.out.println("Testes realizados com sucesso!");

        } catch (Exception e) {
            em.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            em.close();
            emf.close();
        }
    }
}
