package br.com.NexusSaude;

import java.time.LocalDate;

import jakarta.persistence.*;


@Entity
@Table(name = "avaliacoes")
public class Avaliacao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "consulta_id", nullable = false)
    private Consulta consulta;

    
    private Integer nota;

    @Lob
    private String comentario;

    @Column(name = "data_criacao", updatable = false)
    private LocalDate dataCriacao;

    @PrePersist
    public void prePersist() {
        this.dataCriacao = LocalDate.now();
    }

	public Integer getNota() {
		return nota;
	}

	public void setNota(Integer nota) {
		this.nota = nota;
	}

    // Getters e Setters
}
