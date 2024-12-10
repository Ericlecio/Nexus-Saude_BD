package br.com.NexusSaude;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class MainDAO {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("nexus-saude");
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

            // Criar ou buscar a especialidade "Cardiologia"
            Especialidade especialidade = em.createQuery(
                "SELECT e FROM Especialidade e WHERE e.nome = :nome", Especialidade.class)
                .setParameter("nome", "Cardiologia")
                .getResultStream()
                .findFirst()
                .orElse(null);

            if (especialidade == null) {
                System.out.println("Criando uma nova especialidade...");
                especialidade = new Especialidade();
                especialidade.setNome("Cardiologia");
                em.persist(especialidade);
                System.out.println("Especialidade criada com ID: " + especialidade.getId());
            } else {
                System.out.println("Especialidade encontrada: " + especialidade.getNome());
            }

            // Criar um usuário
            Usuario usuario = new Usuario();
            usuario.setNome("Maria Oliveira");
            usuario.setEmail("maria@email.com"); // Defina um valor para o email
            usuario.setSexo("Feminino");
            usuario.setSenha("senha123"); 
            usuario.setTipoUsuario("medico");
            usuario.setStatus("ativo");
            em.persist(usuario);


            // Criar um médico associado ao usuário e à especialidade
            Medico medico = new Medico();
            medico.setUsuario(usuario);
            medico.setEspecialidade(especialidade);
            medico.setCrm("123456");
            medico.setHorariosDisponiveis("Segunda, Quarta, Sexta - 08:00 às 12:00");
            em.persist(medico);

            em.getTransaction().commit();

            System.out.println("Médico criado com ID: " + medico.getId());
        } catch (Exception e) {
            em.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            em.close();
            emf.close();
        }
    }
}
