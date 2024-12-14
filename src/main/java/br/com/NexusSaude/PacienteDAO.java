package br.com.NexusSaude;

import jakarta.persistence.EntityManager;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class PacienteDAO {
    private EntityManager em;

    public PacienteDAO(EntityManager em) {
        this.em = em;
    }

    public void cadastrar(Scanner scanner) {
        System.out.println("\n### CADASTRAR PACIENTE ###");
        System.out.print("Digite o nome do paciente: ");
        String nome = scanner.nextLine();

        System.out.print("Digite o email do paciente: ");
        String email = scanner.nextLine();

        em.getTransaction().begin();

        Usuario usuario = new Usuario();
        usuario.setNome(nome);
        usuario.setEmail(email);
        usuario.setSenha("1234");
        usuario.setTipoUsuario("paciente");
        usuario.setStatus("ativo");
        em.persist(usuario);

        Paciente paciente = new Paciente();
        paciente.setUsuario(usuario);
        paciente.setDataRegistro(LocalDate.now());
        em.persist(paciente);

        em.getTransaction().commit();
        System.out.println("Paciente cadastrado com sucesso! ID: " + paciente.getId());
    }

    public void listar() {
        System.out.println("\n### LISTAR PACIENTES ###");
        List<Paciente> pacientes = em.createQuery("SELECT p FROM Paciente p", Paciente.class).getResultList();
        if (pacientes.isEmpty()) {
            System.out.println("Nenhum paciente encontrado.");
        } else {
            for (Paciente paciente : pacientes) {
                System.out.println("ID: " + paciente.getId() + ", Nome: " + paciente.getUsuario().getNome() +
                        ", Email: " + paciente.getUsuario().getEmail() + 
                        ", Data de Registro: " + paciente.getDataRegistro());
            }
        }
    }

}
