import entity.*;
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
        Monitor monitor = new Monitor();
        BancoRepositorio repo = new BancoRepositorio();
        SlackService slackService = new SlackService(repo);
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
                        if (monitor.verificarCondicao(config)) {

                            String texto = monitor.gerarMensagem(config);

                            slackService.enviarMensagem(config.getBot().getToken(), config.getCanal().getNome(), texto);

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

            try {
                logger.info("[{}] Buscando chamados abertos...", LocalDateTime.now());
                List<Chamado> chamados = repo.buscarChamadosAbertos();

                if (chamados.isEmpty()){
                    logger.info("[{}] Nenhum chamado aberto encontrado.", LocalDateTime.now());
                    return;
                }

                for (Chamado chamado : chamados) {
                    try {
                        logger.info("[{}] Enviando mensagem de chamado aberto para o canal de suporte no Slack...", LocalDateTime.now());
                        slackService.enviarMensagemSuporte(chamado);

                        logger.info("[{}] Chamado aberto enviado para o Slack.", LocalDateTime.now());

                        repo.atualizarChamado(chamado.getId());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        scheduler.scheduleAtFixedRate(tarefa, 0, 5, TimeUnit.MINUTES);
    }
}
