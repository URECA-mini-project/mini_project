package ureca.ureca_mini.chat.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import ureca.ureca_mini.chat.entity.ChatMessageEntity;

import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class RedisSubscriber implements MessageListener {

    @Lazy
    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            String json = new String(message.getBody(), StandardCharsets.UTF_8);
            ChatMessageEntity chatMessage = objectMapper.readValue(json, ChatMessageEntity.class);

            // 변환 성공 시, 웹소켓으로 메시지 전달
            messagingTemplate.convertAndSend("/topic/chatroom/" + chatMessage.getEventId(), chatMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
