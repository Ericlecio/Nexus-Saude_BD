package br.com.NexusSaude;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.time.LocalDate;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class MainCadastroMedicosPacientes {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("nexus-saude");
        EntityManager em = emf.createEntityManager();

        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("### INICIANDO O SISTEMA DE CADASTRO ###");

            boolean continuar = true;
            while (continuar) {
                try {
                    System.out.println("\n### MENU ###");
                    System.out.println("1. Cadastrar Médico");
                    System.out.println("2. Cadastrar Paciente");
                    System.out.println("3. Listar Médicos");
                    System.out.println("4. Listar Pacientes");
                    System.out.println("5. Sair");
                    System.out.print("Escolha uma opção: ");
                    int opcao = scanner.nextInt();
                    scanner.nextLine(); // Limpar buffer

                    switch (opcao) {
                        case 1:
                            cadastrarMedico(em, scanner);
                            break;
                        case 2:
                            cadastrarPaciente(em, scanner);
                            break;
                        case 3:
                            listarMedicos(em);
                            break;
                        case 4:
                            listarPacientes(em);
                            break;
                        case 5:
                            continuar = false;
                            break;
                        default:
                            System.out.println("Opção inválida! Tente novamente.");
                    }
                } catch (InputMismatchException e) {
                    System.out.println("Entrada inválida! Tente novamente.");
                    scanner.nextLine(); // Limpar buffer
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Ocorreu um erro no sistema.");
        } finally {
            em.close();
            emf.close();
            System.out.println("\nConexão encerrada.");
        }
    }

    private static void cadastrarMedico(EntityManager em, Scanner scanner) {
        System.out.println("\n### CADASTRAR MÉDICO ###");

        System.out.print("Digite o nome do médico: ");
        String nome = scanner.nextLine();

        System.out.print("Digite o email do médico: ");
        String email = scanner.nextLine();

        System.out.print("Digite o CRM do médico: ");
        String crm = scanner.nextLine();

        System.out.print("Digite a especialidade do médico: ");
        String especialidadeNome = scanner.nextLine();

        System.out.print("Digite os horários disponíveis do médico: ");
        String horarios = scanner.nextLine();

        em.getTransaction().begin();

        Especialidade especialidade = new Especialidade();
        especialidade.setNome(especialidadeNome);
        em.persist(especialidade);

        Usuario usuario = new Usuario();
        usuario.setNome(nome);
        usuario.setEmail(email);
        usuario.setSenha("1234"); // Senha padrão
        usuario.setTipoUsuario("medico");
        usuario.setStatus("ativo");
        em.persist(usuario);

        Medico medico = new Medico();
        medico.setUsuario(usuario);
        medico.setCrm(crm);
        medico.setEspecialidade(especialidade);
        medico.setHorariosDisponiveis(horarios);
        em.persist(medico);

        em.getTransaction().commit();

        System.out.println("Médico cadastrado com sucesso! ID: " + medico.getId());
    }

    private static void cadastrarPaciente(EntityManager em, Scanner scanner) {
        System.out.println("\n### CADASTRAR PACIENTE ###");

        System.out.print("Digite o nome do paciente: ");
        String nome = scanner.nextLine();

        System.out.print("Digite o email do paciente: ");
        String email = scanner.nextLine();

        em.getTransaction().begin();

        Usuario usuario = new Usuario();
        usuario.setNome(nome);
        usuario.setEmail(email);
        usuario.setSenha("1234"); // Senha padrão
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

    private static void listarMedicos(EntityManager em) {
        System.out.println("\n### LISTAR MÉDICOS ###");

        List<Medico> medicos = em.createQuery("SELECT m FROM Medico m", Medico.class).getResultList();
        if (medicos.isEmpty()) {
            System.out.println("Nenhum médico encontrado.");
        } else {
            for (Medico medico : medicos) {
                System.out.println("ID: " + medico.getId() + ", Nome: " + medico.getUsuario().getNome() + ", CRM: " + medico.getCrm() + ", Especialidade: " + medico.getEspecialidade().getNome());
            }
        }
    }

    private static void listarPacientes(EntityManager em) {
        System.out.println("\n### LISTAR PACIENTES ###");

        List<Paciente> pacientes = em.createQuery("SELECT p FROM Paciente p", Paciente.class).getResultList();
        if (pacientes.isEmpty()) {
            System.out.println("Nenhum paciente encontrado.");
        } else {
            for (Paciente paciente : pacientes) {
                System.out.println("ID: " + paciente.getId() + ", Nome: " + paciente.getUsuario().getNome() + ", Data de Registro: " + paciente.getDataRegistro());
            }
        }
    }
}
