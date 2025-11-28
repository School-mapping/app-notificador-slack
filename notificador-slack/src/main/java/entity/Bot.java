package entity;

public class Bot {
    private Integer id;
    private String nome;
    private String token;

    public Bot() {
    }

    public Bot(Integer id, String nome, String token) {
        this.id = id;
        this.nome = nome;
        this.token = token;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
