package jdbc;

import entity.Configuracao;
import entity.Escola;
import entity.Ideb;
import entity.Verba;
import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.util.List;

public class BancoRepositorio {

//    private static final String URL = System.getenv("DB_URL");
//    private static final String USER = System.getenv("DB_USER");
//    private static final String PASSWORD = System.getenv("DB_PASSWORD");

    private static final String URL = "jdbc:mysql://localhost:3306/teste_slack?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
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

    public List<Configuracao> buscarConfiguracoesAtivas() {
       return getJdbcTemplate().query("SELECT * FROM TB_Notificacao_Config WHERE ativo = true", new BeanPropertyRowMapper<>());
    }

    public Boolean existemNovasVerbas(Timestamp dataUltimoDisparo){
        Integer verbas = getJdbcTemplate().queryForObject("SELECT count(*) FROM TB_Verbas WHERE data_processamento < ?", Integer.class, dataUltimoDisparo);

        return verbas > 0;
    }

    public Boolean existemNovasEscolas(Timestamp dataUltimoDisparo){
        Integer escolas = getJdbcTemplate().queryForObject("SELECT count(*) FROM TB_Escolas WHERE data_processamento < ?", Integer.class, dataUltimoDisparo);

        return escolas > 0;
    }

    public Boolean existemNovasNotas(Timestamp dataUltimoDisparo){
        Integer notas = getJdbcTemplate().queryForObject("SELECT count(*) FROM TB_Ideb WHERE data_processamento < ?", Integer.class,dataUltimoDisparo);

        return notas > 0;
    }

    public void atualizarUltimoDisparo(Configuracao config){
        config.setUltimoDisparo(new Timestamp(System.currentTimeMillis()));
        getJdbcTemplate().update("UPDATE TB_Notificacao_Config SET ultimo_disparo = ? WHERE id = ?", config.getUltimoDisparo(), config.getId());
    }

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }
}