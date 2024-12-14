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

	public void setComentario(String string) {
		// TODO Auto-generated method stub
		
	}

	public void setDataCriacao(LocalDate now) {
		// TODO Auto-generated method stub
		
	}

	public Object getId() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getComentario() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setConsulta(Consulta consulta2) {
		// TODO Auto-generated method stub
		
	}

	public Consulta getConsulta() {
		return consulta;
	}

	public LocalDate getDataCriacao() {
		return dataCriacao;
	}

	public void setId(Long id) {
		this.id = id;
	}
}
