package entity;

import com.slack.api.Slack;
import com.slack.api.methods.MethodsClient;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import com.slack.api.methods.response.chat.ChatPostMessageResponse;
import jdbc.BancoRepositorio;

public class SlackService {
    private final BancoRepositorio bancoRepositorio;

    public SlackService(BancoRepositorio bancoRepositorio) {
        this.bancoRepositorio = bancoRepositorio;
    }

    public void enviarMensagem(String token, String canalId, String mensagem) throws Exception {
        Slack slack = Slack.getInstance();
        MethodsClient methods = slack.methods(token);

        ChatPostMessageRequest request = ChatPostMessageRequest.builder()
                .channel(canalId)
                .text(mensagem)
                .build();

        ChatPostMessageResponse response = methods.chatPostMessage(request);

        if (!response.isOk()) {
            throw new Exception("Erro Slack: " + response.getError());
        }
    }

    public void enviarMensagemSuporte(Chamado chamado) throws Exception {
        String mensagem = "⚠ Novo chamado aberto! \n"
                + "Nº CHAMADO: " + chamado.getId() + "\n"
                + "ASSUNTO: " + chamado.getAssunto() + "\n"
                + "DESCRICAO: " + chamado.getDescricao();

        enviarMensagem(bancoRepositorio.buscarBot().getToken(), "suporte_school_mapping", mensagem);
    }
}
