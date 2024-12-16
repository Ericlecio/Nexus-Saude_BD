package br.com.Entidade;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.*;

@Entity
@Table(name = "pacientes")
public class Paciente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;


    @Lob
    private String historicoMedico;

    @Lob
    private String exames;

    @Column(name = "data_registro", updatable = false)
    private LocalDate dataRegistro = LocalDate.now();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "paciente_plano_saude",
        joinColumns = @JoinColumn(name = "paciente_id"),
        inverseJoinColumns = @JoinColumn(name = "plano_saude_id")
    )
    private List<PlanoSaude> planosSaude;


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

    public String getHistoricoMedico() {
        return historicoMedico;
    }

    public void setHistoricoMedico(String historicoMedico) {
        this.historicoMedico = historicoMedico;
    }

    public String getExames() {
        return exames;
    }

    public void setExames(String exames) {
        this.exames = exames;
    }

    public LocalDate getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(LocalDate dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    public List<PlanoSaude> getPlanosSaude() {
        return planosSaude;
    }

    public void setPlanosSaude(List<PlanoSaude> planosSaude) {
        this.planosSaude = planosSaude;
    }
}
