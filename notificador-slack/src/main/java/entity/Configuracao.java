package entity;

import java.sql.Timestamp;
import java.time.LocalDateTime;

public class Configuracao {
    private Integer id;
    private Integer usuarioId;
    private Bot bot;
    private Canal canal;
    private String tipoAlerta;
    private Boolean ativo;
    private LocalDateTime ultimoDisparo;

    public Configuracao() {
    }

    public Configuracao(Integer id, Integer usuarioId, Bot bot, Canal canal, String tipoAlerta, Boolean ativo, LocalDateTime ultimoDisparo) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.bot = bot;
        this.canal = canal;
        this.tipoAlerta = tipoAlerta;
        this.ativo = ativo;
        this.ultimoDisparo = ultimoDisparo;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Integer usuarioId) {
        this.usuarioId = usuarioId;
    }

    public Bot getBot() {
        return bot;
    }

    public void setBot(Bot bot) {
        this.bot = bot;
    }

    public Canal getCanal() {
        return canal;
    }

    public void setCanal(Canal canal) {
        this.canal = canal;
    }

    public String getTipoAlerta() {
        return tipoAlerta;
    }

    public void setTipoAlerta(String tipoAlerta) {
        this.tipoAlerta = tipoAlerta;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public LocalDateTime getUltimoDisparo() {
        return ultimoDisparo;
    }

    public void setUltimoDisparo(LocalDateTime ultimoDisparo) {
        this.ultimoDisparo = ultimoDisparo;
    }
}
