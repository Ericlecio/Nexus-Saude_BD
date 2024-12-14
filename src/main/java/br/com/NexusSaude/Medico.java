package br.com.NexusSaude;

import jakarta.persistence.*;
import java.time.LocalTime;
import java.util.List;

@Entity
@Table(name = "medicos")
public class Medico {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER) 
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "especialidade_id", nullable = false)
    private Especialidade especialidade;

    @Column(nullable = false, unique = true)
    private String crm;

    private Double valorConsulta;
    private Double avaliacao;
    private String consultorio;

    @Lob
    private String horariosDisponiveis;

    @ElementCollection
    @CollectionTable(name = "medico_dias_atendimento", joinColumns = @JoinColumn(name = "medico_id"))
    @Column(name = "dia_atendimento")
    private List<String> diasAtendimento;

    private LocalTime horarioInicioExpediente;
    private LocalTime horarioFimExpediente;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Usuario getUsuario() {
		return usuario;
	}
	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}
	public Especialidade getEspecialidade() {
		return especialidade;
	}
	public void setEspecialidade(Especialidade especialidade) {
		this.especialidade = especialidade;
	}
	public String getCrm() {
		return crm;
	}
	public void setCrm(String crm) {
		this.crm = crm;
	}
	public Double getValorConsulta() {
		return valorConsulta;
	}
	public void setValorConsulta(Double valorConsulta) {
		this.valorConsulta = valorConsulta;
	}
	public Double getAvaliacao() {
		return avaliacao;
	}
	public void setAvaliacao(Double avaliacao) {
		this.avaliacao = avaliacao;
	}
	public String getConsultorio() {
		return consultorio;
	}
	public void setConsultorio(String consultorio) {
		this.consultorio = consultorio;
	}
	public String getHorariosDisponiveis() {
		return horariosDisponiveis;
	}
	public void setHorariosDisponiveis(String horariosDisponiveis) {
		this.horariosDisponiveis = horariosDisponiveis;
	}
	public List<String> getDiasAtendimento() {
		return diasAtendimento;
	}
	public void setDiasAtendimento(List<String> diasAtendimento) {
		this.diasAtendimento = diasAtendimento;
	}
	public LocalTime getHorarioInicioExpediente() {
		return horarioInicioExpediente;
	}
	public void setHorarioInicioExpediente(LocalTime horarioInicioExpediente) {
		this.horarioInicioExpediente = horarioInicioExpediente;
	}
	public LocalTime getHorarioFimExpediente() {
		return horarioFimExpediente;
	}
	public void setHorarioFimExpediente(LocalTime horarioFimExpediente) {
		this.horarioFimExpediente = horarioFimExpediente;
	}


}
