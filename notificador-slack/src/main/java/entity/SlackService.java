package entity;

import com.slack.api.Slack;
import com.slack.api.methods.MethodsClient;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import com.slack.api.methods.response.chat.ChatPostMessageResponse;

public class SlackService {
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
}
