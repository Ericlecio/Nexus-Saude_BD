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
            System.out.println("3. Voltar");
            System.out.print("Escolha uma opção: ");

            try {
                int opcao = scanner.nextInt();
                scanner.nextLine(); // Limpar buffer

                switch (opcao) {
                    case 1:
                        avaliarConsulta(em, consultaDAO, avaliacaoDAO, scanner);
                        break;

                    case 2:
                        listarAvaliacoes(avaliacaoDAO);
                        break;

                    case 3:
                        continuar = false;
                        break;
                    default:
                        System.out.println("Opção inválida! Tente novamente.");
                }
            } catch (Exception e) {
                System.out.println("Entrada inválida! Por favor, tente novamente.");
                scanner.nextLine(); // Limpar buffer após erro
            }
        }
    }

    private static void avaliarConsulta(EntityManager em, ConsultaDAO consultaDAO, AvaliacaoDAO avaliacaoDAO, Scanner scanner) {
        System.out.println("\n### AVALIAR CONSULTA ###");

        // Listar consultas realizadas
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

        // Criar e salvar a avaliação
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

    
    }
