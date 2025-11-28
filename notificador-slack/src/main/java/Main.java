import entity.Bot;
import entity.Configuracao;
import entity.Monitor;
import entity.SlackService;
import jdbc.BancoRepositorio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        SlackService slackService = new SlackService();
        Monitor monitor = new Monitor();
        BancoRepositorio repo = new BancoRepositorio();
        Logger logger = LoggerFactory.getLogger(Main.class);

        Runnable tarefa = () -> {
            try {
                logger.info("[{}] Buscando configurações de notificação ativas...", LocalDateTime.now());
                List<Configuracao> configs = repo.buscarConfiguracoesAtivas();

                if (configs.isEmpty()) {
                    logger.info("[{}] Nenhuma configuração ativa encontrada.", LocalDateTime.now());
                    return;
                }

                for (Configuracao config : configs) {
                    try {
                        // 1. Verifica se a regra de negócio aconteceu (Verba chegou? Ideb caiu?)
                        if (monitor.verificarCondicao(config)) {

                            // 2. Gera o texto
                            String texto = monitor.gerarMensagem(config);

                            // 3. Envia usando o token ESPECÍFICO daquela config
                            slackService.enviarMensagem(config.getBot().getToken(), config.getCanal().getNome(), texto);

                            // 4. Atualiza data do último disparo
                            repo.atualizarUltimoDisparo(config);

                            logger.info("[{}] Notificação enviada para config ID: {}", LocalDateTime.now(), config.getId());
                            repo.getJdbcTemplate().update("INSERT INTO TB_Logs(data_hora, nivel, descricao, origem) VALUES (?, ?, ?, ?)", LocalDateTime.now(), "INFO", "Notificação enviada para config ID: " + config.getId(), "SLACK");
                        } else {
                            logger.info("[{}] Nenhuma notificação enviada para config ID: {}", LocalDateTime.now(), config.getId());
                            repo.getJdbcTemplate().update("INSERT INTO TB_Logs(data_hora, nivel, descricao, origem) VALUES (?, ?, ?, ?)", LocalDateTime.now(), "INFO", "Nenhuma notificação enviada para config ID: " + config.getId(), "SLACK");
                        }
                    } catch (Exception e) {
                        logger.error("[{}] ❌ Falha ao processar config: {} \n {}", LocalDateTime.now(), config.getId(), e.getMessage());
                        repo.getJdbcTemplate().update("INSERT INTO TB_Logs(data_hora, nivel, descricao, origem) VALUES (?, ?, ?, ?)", LocalDateTime.now(), "ERROR", "Falha ao processar config " + config.getId() + ": " + e.getMessage(), "SLACK");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        // Roda a cada 5 minutos
        scheduler.scheduleAtFixedRate(tarefa, 0, 5, TimeUnit.MINUTES);
    }
}
