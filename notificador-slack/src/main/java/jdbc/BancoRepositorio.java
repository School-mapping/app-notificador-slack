package jdbc;

import entity.*;
import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.cglib.core.Local;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

public class BancoRepositorio {

//    private static final String URL = System.getenv("DB_URL");
//    private static final String USER = System.getenv("DB_USER");
//    private static final String PASSWORD = System.getenv("DB_PASSWORD");

    private static final String URL = "jdbc:mysql://localhost:3306/SchoolMapping?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "admin";



    private final DataSource dataSource;
    private final JdbcTemplate jdbcTemplate;

    public BancoRepositorio() {

        BasicDataSource basicDataSource = new BasicDataSource();

        basicDataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        basicDataSource.setUrl(URL);
        basicDataSource.setUsername(USER);
        basicDataSource.setPassword(PASSWORD);

//        basicDataSource.setUrl(System.getenv("DB_URL"));
//        basicDataSource.setUsername(System.getenv("DB_USER"));
//        basicDataSource.setPassword(System.getenv("DB_PASSWORD"));

        this.dataSource = basicDataSource;
        this.jdbcTemplate = new JdbcTemplate(this.dataSource);
    }

    public List<Chamado> buscarChamadosAbertos() {
        String sql = "SELECT * FROM TB_Chamados WHERE notificado = false";
        return getJdbcTemplate().query(sql, new BeanPropertyRowMapper<>(Chamado.class));
    }

    public void atualizarChamado(Integer id) {
        String sql = "UPDATE TB_Chamados SET notificado = true WHERE id = ?";
        getJdbcTemplate().update(sql, id);
    }

    public List<Configuracao> buscarConfiguracoesAtivas() {
        String sql = "SELECT c.*, b.id as bot_id, b.nome as bot_nome, b.token as bot_token, " +
                    "ch.id as canal_id, ch.nome as canal_nome " +
                    "FROM TB_Notificacao_Config c " +
                    "LEFT JOIN TB_Bot_Slack b ON c.id_bot = b.id " +
                    "LEFT JOIN TB_Canal_Slack ch ON c.id_canal = ch.id " +
                    "WHERE c.ativo = true";
        
        return getJdbcTemplate().query(sql, (rs, rowNum) -> {
            Configuracao config = new Configuracao();
            config.setId(rs.getInt("id"));
            config.setUsuarioId(rs.getInt("id_usuario"));
            config.setTipoAlerta(rs.getString("tipo_alerta"));
            config.setAtivo(rs.getBoolean("ativo"));
            Timestamp timestamp = rs.getTimestamp("ultimo_disparo");
            config.setUltimoDisparo(timestamp != null ? timestamp.toLocalDateTime() : null);
            
            // Set Bot
            Bot bot = new Bot();
            bot.setId(rs.getInt("bot_id"));
            bot.setNome(rs.getString("bot_nome"));
            bot.setToken(rs.getString("bot_token"));
            config.setBot(bot);
            
            // Set Canal
            Canal canal = new Canal();
            canal.setId(rs.getInt("canal_id"));
            canal.setNome(rs.getString("canal_nome"));
            config.setCanal(canal);
            
            return config;
        });
    }

    public Boolean existemNovasVerbas(LocalDateTime dataUltimoDisparo){
        Integer verbas = getJdbcTemplate().queryForObject("SELECT count(*) FROM TB_Verbas WHERE data_processamento > ?", Integer.class, dataUltimoDisparo);

        return verbas > 0;
    }

    public Boolean existemNovasEscolas(LocalDateTime dataUltimoDisparo){
        Integer escolas = getJdbcTemplate().queryForObject("SELECT count(*) FROM TB_Escolas WHERE data_processamento > ?", Integer.class, dataUltimoDisparo);

        return escolas > 0;
    }

    public Boolean existemNovasNotas(LocalDateTime dataUltimoDisparo){
        Integer notas = getJdbcTemplate().queryForObject("SELECT count(*) FROM TB_Ideb WHERE data_processamento > ?", Integer.class, dataUltimoDisparo);

        return notas > 0;
    }

    public void atualizarUltimoDisparo(Configuracao config){
        config.setUltimoDisparo(LocalDateTime.now());
        getJdbcTemplate().update("UPDATE TB_Notificacao_Config SET ultimo_disparo = ? WHERE id = ?", config.getUltimoDisparo(), config.getId());
    }

    public Bot buscarBot() {
        String sql = "SELECT id, nome, token FROM TB_Bot_Slack WHERE id = 1";
        
        try {
            return getJdbcTemplate().queryForObject(
                sql,
                (rs, rowNum) -> {
                    Bot bot = new Bot();
                    bot.setId(rs.getInt("id"));
                    bot.setNome(rs.getString("nome"));
                    bot.setToken(rs.getString("token"));
                    return bot;
                }
            );
        } catch (Exception e) {
            return null;
        }
    }

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }
}