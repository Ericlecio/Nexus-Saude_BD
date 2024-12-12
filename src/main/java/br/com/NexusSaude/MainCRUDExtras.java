package br.com.NexusSaude;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.time.LocalDate;
import java.util.List;

public class MainCRUDExtras {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("nexus-saude");
        EntityManager em = emf.createEntityManager();

        try {
            System.out.println("### INICIANDO OPERAÇÕES COM AVALIAÇÃO, PAGAMENTO E PLANO DE SAÚDE ###");

            // === INSERIR ===
            System.out.println("\n### INSERIR ###");
            em.getTransaction().begin();

            System.out.println("Criando um plano de saúde...");
            PlanoSaude planoSaude = new PlanoSaude();
            planoSaude.setNome("Plano Ouro");
            planoSaude.setNumeroContrato("123456789");
            planoSaude.setStatus("Ativo");
            planoSaude.setDataInicio(LocalDate.now());
            planoSaude.setDataFim(LocalDate.now().plusYears(1));
            em.persist(planoSaude);
            System.out.println("Plano de saúde criado: ID " + planoSaude.getId() + ", Nome: " + planoSaude.getNome());

            System.out.println("Criando um pagamento...");
            Pagamento pagamento = new Pagamento();
            pagamento.setValorPago(250.00);
            pagamento.setFormaPagamento("Cartão de Crédito");
            pagamento.setStatus("Concluído");
            pagamento.setDataPagamento(LocalDate.now());
            em.persist(pagamento);
            System.out.println("Pagamento criado: ID " + pagamento.getId() + ", Valor: " + pagamento.getValorPago());

            System.out.println("Criando uma avaliação...");
            Avaliacao avaliacao = new Avaliacao();
            avaliacao.setNota(5);
            avaliacao.setComentario("Excelente atendimento!");
            avaliacao.setDataCriacao(LocalDate.now());
            em.persist(avaliacao);
            System.out.println("Avaliação criada: ID " + avaliacao.getId() + ", Nota: " + avaliacao.getNota());

            em.getTransaction().commit();
            System.out.println("Inserção concluída com sucesso.");

            // === ATUALIZAR ===
            System.out.println("\n### ATUALIZAR ###");
            em.getTransaction().begin();

            System.out.println("Buscando o plano de saúde para atualizar...");
            PlanoSaude planoParaAtualizar = em.find(PlanoSaude.class, planoSaude.getId());
            if (planoParaAtualizar != null) {
                System.out.println("Plano encontrado: ID " + planoParaAtualizar.getId() + ", Status: " + planoParaAtualizar.getStatus());
                planoParaAtualizar.setStatus("Inativo");
                em.merge(planoParaAtualizar);
                System.out.println("Plano atualizado: Novo Status: " + planoParaAtualizar.getStatus());
            } else {
                System.out.println("Plano não encontrado para atualização.");
            }

            em.getTransaction().commit();
            System.out.println("Atualização concluída com sucesso.");

            // === CONSULTAR ===
            System.out.println("\n### CONSULTAR ###");
            System.out.println("Buscando todos os pagamentos...");
            List<Pagamento> pagamentos = em.createQuery("SELECT p FROM Pagamento p", Pagamento.class).getResultList();
            if (!pagamentos.isEmpty()) {
                for (Pagamento p : pagamentos) {
                    System.out.println("Pagamento: ID " + p.getId() + ", Valor: " + p.getValorPago() + ", Status: " + p.getStatus());
                }
            } else {
                System.out.println("Nenhum pagamento encontrado.");
            }

            // === DELETAR ===
            System.out.println("\n### DELETAR ###");
            em.getTransaction().begin();

            System.out.println("Buscando a avaliação para exclusão...");
            Avaliacao avaliacaoParaExcluir = em.find(Avaliacao.class, avaliacao.getId());
            if (avaliacaoParaExcluir != null) {
                System.out.println("Avaliação encontrada: ID " + avaliacaoParaExcluir.getId() + ", Nota: " + avaliacaoParaExcluir.getNota());
                em.remove(avaliacaoParaExcluir);
                System.out.println("Avaliação com ID " + avaliacaoParaExcluir.getId() + " excluída.");
            } else {
                System.out.println("Avaliação não encontrada para exclusão.");
            }

            em.getTransaction().commit();
            System.out.println("Exclusão concluída com sucesso.");

            System.out.println("\n### TODAS AS OPERAÇÕES FORAM REALIZADAS COM SUCESSO ###");

        } catch (Exception e) {
            em.getTransaction().rollback();
            e.printStackTrace();
            System.out.println("Ocorreu um erro durante as operações. Alterações revertidas.");
        } finally {
            em.close();
            emf.close();
            System.out.println("\nConexão encerrada.");
        }
    }
}
