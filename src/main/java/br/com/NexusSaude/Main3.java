package br.com.NexusSaude;

import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Scanner;

public class Main3 {

    public static void main(String[] args) {
        EntityManager em = JPAUtil.getEntityManager();
        Scanner scanner = new Scanner(System.in);

        try {
            menuComentariosEavaliacoes(em, scanner);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Erro no sistema de avaliações e comentários.");
        } finally {
            em.close();
            scanner.close();
        }
    }

    public static void menuComentariosEavaliacoes(EntityManager em, Scanner scanner) {
        ConsultaDAO consultaDAO = new ConsultaDAO(em);
        AvaliacaoDAO avaliacaoDAO = new AvaliacaoDAO(em);

        boolean continuar = true;

        while (continuar) {
            System.out.println("\n### MENU AVALIAÇÕES E COMENTÁRIOS ###");
            System.out.println("1. Avaliar Consulta");
            System.out.println("2. Listar Avaliações");
            System.out.println("3. Adicionar Comentário em Avaliação");
            System.out.println("4. Listar Comentários por Avaliação");
            System.out.println("5. Atualizar Avaliação");
            System.out.println("6. Remover Avaliação");
            System.out.println("7. Voltar");
            System.out.print("Escolha uma opção: ");

            try {
                int opcao = scanner.nextInt();
                scanner.nextLine();

                switch (opcao) {
                    case 1:
                        avaliarConsulta(em, consultaDAO, avaliacaoDAO, scanner);
                        break;

                    case 2:
                        listarAvaliacoes(avaliacaoDAO);
                        break;

                    case 3:
                        adicionarComentario(em, avaliacaoDAO, scanner);
                        break;

                    case 4:
                        listarComentariosPorAvaliacao(em, avaliacaoDAO, scanner);
                        break;

                    case 5:
                        atualizarAvaliacao(em, avaliacaoDAO, scanner);
                        break;

                    case 6:
                        removerAvaliacao(em, avaliacaoDAO, scanner);
                        break;

                    case 7:
                        continuar = false;
                        break;

                    default:
                        System.out.println("Opção inválida! Tente novamente.");
                }
            } catch (Exception e) {
                System.out.println("Entrada inválida! Por favor, tente novamente.");
                scanner.nextLine();
            }
        }
    }

    private static void avaliarConsulta(EntityManager em, ConsultaDAO consultaDAO, AvaliacaoDAO avaliacaoDAO, Scanner scanner) {
        System.out.println("\n### AVALIAR CONSULTA ###");

        List<Consulta> consultas = consultaDAO.listar();
        if (consultas.isEmpty()) {
            System.out.println("Nenhuma consulta encontrada.");
            return;
        }

        System.out.println("Consultas disponíveis para avaliação:");
        for (Consulta consulta : consultas) {
            if ("Paga".equals(consulta.getStatus())) {
                System.out.println("ID: " + consulta.getId() + ", Paciente: " + consulta.getPaciente().getUsuario().getNome() +
                        ", Médico: " + consulta.getMedico().getUsuario().getNome() +
                        ", Data: " + consulta.getDataConsulta());
            }
        }

        System.out.print("Digite o ID da consulta para avaliar: ");
        long consultaId = scanner.nextLong();
        scanner.nextLine();

        Consulta consulta = em.find(Consulta.class, consultaId);
        if (consulta == null || !"Paga".equals(consulta.getStatus())) {
            System.out.println("Consulta inválida ou ainda não foi paga. Operação cancelada.");
            return;
        }

        System.out.print("Digite a nota da avaliação (0 a 10): ");
        int nota = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Digite um comentário sobre a consulta: ");
        String comentarioTexto = scanner.nextLine();

        Avaliacao avaliacao = new Avaliacao();
        avaliacao.setConsulta(consulta);
        avaliacao.setNota(nota);
        avaliacao.setComentario(comentarioTexto);

        avaliacaoDAO.salvar(avaliacao);

        System.out.println("Avaliação salva com sucesso! ID da Avaliação: " + avaliacao.getId());
    }

    private static void listarAvaliacoes(AvaliacaoDAO avaliacaoDAO) {
        System.out.println("\n### LISTAR AVALIAÇÕES ###");

        List<Avaliacao> avaliacoes = avaliacaoDAO.listar();
        if (avaliacoes.isEmpty()) {
            System.out.println("Nenhuma avaliação encontrada.");
            return;
        }

        for (Avaliacao avaliacao : avaliacoes) {
            System.out.println("ID: " + avaliacao.getId() + ", Nota: " + avaliacao.getNota() +
                    ", Comentário: " + avaliacao.getComentario() +
                    ", Data: " + avaliacao.getDataCriacao());
        }
    }

    private static void adicionarComentario(EntityManager em, AvaliacaoDAO avaliacaoDAO, Scanner scanner) {
        System.out.println("\n### ADICIONAR COMENTÁRIO ###");

        List<Avaliacao> avaliacoes = avaliacaoDAO.listar();
        if (avaliacoes.isEmpty()) {
            System.out.println("Nenhuma avaliação encontrada.");
            return;
        }

        System.out.println("Avaliações disponíveis:");
        for (Avaliacao avaliacao : avaliacoes) {
            System.out.println("ID: " + avaliacao.getId() + ", Nota: " + avaliacao.getNota() + ", Comentário: " + avaliacao.getComentario());
        }

        System.out.print("Digite o ID da avaliação para adicionar um comentário: ");
        long avaliacaoId = scanner.nextLong();
        scanner.nextLine();

        Avaliacao avaliacao = avaliacaoDAO.buscarPorId(avaliacaoId);
        if (avaliacao == null) {
            System.out.println("Avaliação não encontrada. Operação cancelada.");
            return;
        }

        System.out.print("Digite o texto do comentário: ");
        String textoComentario = scanner.nextLine();

        Comentario comentario = new Comentario();
        comentario.setAvaliacao(avaliacao);
        comentario.setTexto(textoComentario);

        em.getTransaction().begin();
        em.persist(comentario);
        em.getTransaction().commit();

        System.out.println("Comentário adicionado com sucesso!");
    }

    private static void listarComentariosPorAvaliacao(EntityManager em, AvaliacaoDAO avaliacaoDAO, Scanner scanner) {
        System.out.println("\n### LISTAR COMENTÁRIOS POR AVALIAÇÃO ###");

        List<Avaliacao> avaliacoes = avaliacaoDAO.listar();
        if (avaliacoes.isEmpty()) {
            System.out.println("Nenhuma avaliação encontrada.");
            return;
        }

        System.out.println("Avaliações disponíveis:");
        for (Avaliacao avaliacao : avaliacoes) {
            System.out.println("ID: " + avaliacao.getId() + ", Nota: " + avaliacao.getNota());
        }

        System.out.print("Digite o ID da avaliação para listar os comentários: ");
        long avaliacaoId = scanner.nextLong();
        scanner.nextLine();

        Avaliacao avaliacao = em.find(Avaliacao.class, avaliacaoId);

        if (avaliacao == null) {
            System.out.println("Avaliação não encontrada. Operação cancelada.");
            return;
        }

        List<Comentario> comentarios = avaliacao.getComentarios();
        comentarios.size();

        if (comentarios.isEmpty()) {
            System.out.println("Nenhum comentário encontrado para esta avaliação.");
            return;
        }

        System.out.println("Comentários para a avaliação ID: " + avaliacaoId);
        for (Comentario comentario : comentarios) {
            System.out.println("ID: " + comentario.getId() + ", Texto: " + comentario.getTexto() + ", Data: " + comentario.getDataCriacao());
        }
    }


    private static void atualizarAvaliacao(EntityManager em, AvaliacaoDAO avaliacaoDAO, Scanner scanner) {
        System.out.println("\n### ATUALIZAR AVALIAÇÃO ###");

        List<Avaliacao> avaliacoes = avaliacaoDAO.listar();
        if (avaliacoes.isEmpty()) {
            System.out.println("Nenhuma avaliação encontrada.");
            return;
        }

        System.out.println("Avaliações disponíveis:");
        for (Avaliacao avaliacao : avaliacoes) {
            System.out.println("ID: " + avaliacao.getId() + ", Nota: " + avaliacao.getNota() + ", Comentário: " + avaliacao.getComentario());
        }

        System.out.print("Digite o ID da avaliação para atualizar: ");
        long avaliacaoId = scanner.nextLong();
        scanner.nextLine();

        Avaliacao avaliacao = avaliacaoDAO.buscarPorId(avaliacaoId);
        if (avaliacao == null) {
            System.out.println("Avaliação não encontrada. Operação cancelada.");
            return;
        }

        System.out.print("Digite a nova nota (0 a 10): ");
        int novaNota = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Digite o novo comentário: ");
        String novoComentario = scanner.nextLine();

        avaliacao.setNota(novaNota);
        avaliacao.setComentario(novoComentario);

        avaliacaoDAO.atualizar(avaliacao);
        System.out.println("Avaliação atualizada com sucesso!");
    }

    private static void removerAvaliacao(EntityManager em, AvaliacaoDAO avaliacaoDAO, Scanner scanner) {
        System.out.println("\n### REMOVER AVALIAÇÃO ###");

        List<Avaliacao> avaliacoes = avaliacaoDAO.listar();
        if (avaliacoes.isEmpty()) {
            System.out.println("Nenhuma avaliação encontrada.");
            return;
        }

        System.out.println("Avaliações disponíveis:");
        for (Avaliacao avaliacao : avaliacoes) {
            System.out.println("ID: " + avaliacao.getId() + ", Nota: " + avaliacao.getNota() + ", Comentário: " + avaliacao.getComentario());
        }

        System.out.print("Digite o ID da avaliação para remover: ");
        long avaliacaoId = scanner.nextLong();
        scanner.nextLine();

        avaliacaoDAO.remover(avaliacaoId);
        System.out.println("Avaliação removida com sucesso!");
    }
}
