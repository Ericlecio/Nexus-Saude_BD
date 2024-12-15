package br.com.NexusSaude;

import jakarta.persistence.EntityManager;

import java.time.LocalDate;
import java.util.ArrayList;
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

        List<PlanoSaude> planos = new ArrayList<>();
        boolean continuar = true;

        while (continuar) {
            System.out.println("\n### PLANOS DE SAÚDE ###");
            System.out.println("1. Associar plano existente");
            System.out.println("2. Cadastrar novo plano");
            System.out.println("3. Finalizar seleção de planos");
            System.out.print("Escolha uma opção: ");
            int opcao = scanner.nextInt();
            scanner.nextLine();

            switch (opcao) {
                case 1:
                    System.out.print("Digite o ID do plano a ser associado: ");
                    Long idPlano = scanner.nextLong();
                    scanner.nextLine();
                    PlanoSaude planoExistente = em.find(PlanoSaude.class, idPlano);
                    if (planoExistente != null) {
                        planos.add(planoExistente);
                        System.out.println("Plano associado com sucesso!");
                    } else {
                        System.out.println("Plano não encontrado.");
                    }
                    break;

                case 2:
                    System.out.print("Digite o nome do novo plano: ");
                    String nomePlano = scanner.nextLine();
                    PlanoSaude novoPlano = new PlanoSaude();
                    novoPlano.setNome(nomePlano);
                    em.persist(novoPlano);
                    planos.add(novoPlano);
                    System.out.println("Novo plano cadastrado e associado com sucesso!");
                    break;

                case 3:
                    continuar = false;
                    break;

                default:
                    System.out.println("Opção inválida.");
            }
        }

        Paciente paciente = new Paciente();
        paciente.setUsuario(usuario);
        paciente.setPlanosSaude(planos);
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
                System.out.println("Planos de Saúde: ");
                for (PlanoSaude plano : paciente.getPlanosSaude()) {
                    System.out.println(" - " + plano.getNome());
                }
            }
        }
    }

    public void deletar(Scanner scanner) {
        System.out.println("\n### DELETAR PACIENTE ###");
        System.out.print("Digite o ID do paciente: ");
        Long id = scanner.nextLong();
        scanner.nextLine();

        em.getTransaction().begin();
        Paciente paciente = em.find(Paciente.class, id);
        if (paciente != null) {
            em.remove(paciente);
            em.getTransaction().commit();
            System.out.println("Paciente deletado com sucesso!");
        } else {
            em.getTransaction().rollback();
            System.out.println("Paciente não encontrado.");
        }
    }

    public void atualizar(Scanner scanner) {
        System.out.println("\n### ATUALIZAR PACIENTE ###");
        System.out.print("Digite o ID do paciente: ");
        Long id = scanner.nextLong();
        scanner.nextLine();

        Paciente paciente = em.find(Paciente.class, id);
        if (paciente != null) {
            System.out.print("Digite o novo nome: ");
            String novoNome = scanner.nextLine();
            System.out.print("Digite o novo email: ");
            String novoEmail = scanner.nextLine();

            em.getTransaction().begin();
            paciente.getUsuario().setNome(novoNome);
            paciente.getUsuario().setEmail(novoEmail);
            em.getTransaction().commit();
            System.out.println("Paciente atualizado com sucesso!");
        } else {
            System.out.println("Paciente não encontrado.");
        }
    }
}
