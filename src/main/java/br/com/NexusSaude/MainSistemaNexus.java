package br.com.NexusSaude;

import java.util.Scanner;

import jakarta.persistence.EntityManager;

public class MainSistemaNexus {
    public static void main(String[] args) {
        EntityManager em = JPAUtil.getEntityManager();
        Scanner scanner = new Scanner(System.in);

        boolean continuar = true;
        while (continuar) {
            System.out.println("\n### MENU PRINCIPAL ###");
            System.out.println("1. Sistema de Cadastro");
            System.out.println("2. Sistema de Agendamento");
            System.out.println("3. Sair");
            System.out.print("Escolha uma opção: ");

            int opcao = scanner.nextInt();
            scanner.nextLine(); // Limpar buffer

            switch (opcao) {
                case 1:
                    Main1.menuCadastro(em, scanner);
                    break;
                case 2:
                    Main2.menuAgendamento(em, scanner);
                    break;
                case 3:
                    continuar = false;
                    break;
                default:
                    System.out.println("Opção inválida! Tente novamente.");
            }
        }

        em.close();
        scanner.close();
        JPAUtil.close();
    }
}
