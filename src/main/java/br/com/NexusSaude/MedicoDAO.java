package br.com.NexusSaude;

import jakarta.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MedicoDAO {
    private EntityManager em;

    public MedicoDAO(EntityManager em) {
        this.em = em;
    }

    public void cadastrar(Scanner scanner) {
        System.out.println("\n### CADASTRAR MÉDICO ###");
        System.out.print("Digite o nome do médico: ");
        String nome = scanner.nextLine();

        System.out.print("Digite o email do médico: ");
        String email = scanner.nextLine();

        System.out.print("Digite o CRM do médico: ");
        String crm = scanner.nextLine();

        System.out.print("Digite a especialidade do médico: ");
        String especialidadeNome = scanner.nextLine();

        em.getTransaction().begin();

        Especialidade especialidade = new Especialidade();
        especialidade.setNome(especialidadeNome);
        em.persist(especialidade);

        Usuario usuario = new Usuario();
        usuario.setNome(nome);
        usuario.setEmail(email);
        usuario.setSenha("1234");
        usuario.setTipoUsuario("medico");
        usuario.setStatus("ativo");
        em.persist(usuario);

        Medico medico = new Medico();
        medico.setUsuario(usuario);
        medico.setCrm(crm);
        medico.setEspecialidade(especialidade);

        List<String> diasAtendimento = new ArrayList<>();
        boolean adicionarMaisDias = true;

        while (adicionarMaisDias) {
            System.out.println("\nEscolha um dia da semana (1- Segunda, 2- Terça, 3- Quarta, 4- Quinta, 5- Sexta): ");
            int diaEscolhido = scanner.nextInt();
            scanner.nextLine();

            if (diaEscolhido < 1 || diaEscolhido > 5) {
                System.out.println("Opção inválida! Tente novamente.");
                continue;
            }

            String dia = "";
            switch (diaEscolhido) {
                case 1: dia = "Segunda-feira"; break;
                case 2: dia = "Terça-feira"; break;
                case 3: dia = "Quarta-feira"; break;
                case 4: dia = "Quinta-feira"; break;
                case 5: dia = "Sexta-feira"; break;
            }

            System.out.print("Digite o horário de início para " + dia + " (HH:mm): ");
            String horarioInicio = scanner.nextLine();

            System.out.print("Digite o horário de fim para " + dia + " (HH:mm): ");
            String horarioFim = scanner.nextLine();

            diasAtendimento.add(dia + ": " + horarioInicio + " - " + horarioFim);

            System.out.print("Deseja adicionar mais dias de atendimento? (S/N): ");
            String resposta = scanner.nextLine();
            if (resposta.equalsIgnoreCase("N")) {
                adicionarMaisDias = false;
            }
        }

        medico.setDiasAtendimento(diasAtendimento);
        em.persist(medico);

        em.getTransaction().commit();
        System.out.println("Médico cadastrado com sucesso! ID: " + medico.getId());
    }

    public void listar() {
        System.out.println("\n### LISTAR MÉDICOS ###");
        List<Medico> medicos = em.createQuery("SELECT m FROM Medico m", Medico.class).getResultList();
        if (medicos.isEmpty()) {
            System.out.println("Nenhum médico encontrado.");
        } else {
            for (Medico medico : medicos) {
                System.out.println("ID: " + medico.getId() + ", Nome: " + medico.getUsuario().getNome() +
                        ", CRM: " + medico.getCrm() + ", Especialidade: " + medico.getEspecialidade().getNome() +
                        ", Dias e Horários de Atendimento: ");
                for (String diaAtendimento : medico.getDiasAtendimento()) {
                    System.out.println("   " + diaAtendimento);
                }
            }
        }
    }
}
