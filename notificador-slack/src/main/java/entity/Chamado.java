package entity;

import java.time.LocalDate;

public class Chamado {
    private Integer id;
    private Integer idUsuario;
    private String assunto;
    private String descricao;
    private String status;
    private Boolean notificado;
    private LocalDate dataChamado;

    public Chamado() {

    }

    public Chamado(Integer idUsuario, String assunto, String descricao) {
        this();
        this.idUsuario = idUsuario;
        this.assunto = assunto;
        this.descricao = descricao;
    }

    public Chamado(Integer id, Integer idUsuario, String assunto, String descricao, String status, Boolean notificado, LocalDate dataChamado) {
        this.id = id;
        this.idUsuario = idUsuario;
        this.assunto = assunto;
        this.descricao = descricao;
        this.status = status;
        this.notificado = notificado;
        this.dataChamado = dataChamado;
    }


    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getAssunto() {
        return assunto;
    }

    public void setAssunto(String assunto) {
        this.assunto = assunto;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean getNotificado() {
        return notificado;
    }

    public void setNotificado(Boolean notificado) {
        this.notificado = notificado;
    }

    public LocalDate getDataChamado() {
        return dataChamado;
    }

    public void setDataChamado(LocalDate dataChamado) {
        this.dataChamado = dataChamado;
    }

    @Override
    public String toString() {
        return "Chamado{" +
                "id=" + id +
                ", idUsuario=" + idUsuario +
                ", assunto='" + assunto + '\'' +
                ", descricao='" + descricao + '\'' +
                ", status='" + status + '\'' +
                ", notificado=" + notificado +
                ", dataChamado=" + dataChamado +
                '}';
    }
}
