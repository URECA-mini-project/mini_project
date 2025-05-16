package ureca.ureca_mini.chat;


import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;
import ureca.ureca_mini.chat.entity.ChatMessageEntity;
import ureca.ureca_mini.user.jwt.JWTUtil;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class JwtChannelInterceptor implements ChannelInterceptor {

    private final JWTUtil jwtUtil;
    private final ApplicationContext applicationContext;
    private SimpMessagingTemplate messagingTemplate;

    private SimpMessagingTemplate getMessagingTemplate() {
        if (messagingTemplate == null) {
            messagingTemplate = applicationContext.getBean(SimpMessagingTemplate.class);
        }
        return messagingTemplate;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            String token = accessor.getFirstNativeHeader("Authorization");

            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);

                try {
                    if (!jwtUtil.isExpired(token)) {
                        String username = jwtUtil.getUsername(token);
                        accessor.setUser(() -> username);

                        String eventIdHeader = accessor.getFirstNativeHeader("eventId");
                        int eventId = Integer.parseInt(eventIdHeader);

                        new Thread(() -> {
                            try {
                                Thread.sleep(300); // 💡 핵심 지연 포인트
                                ChatMessageEntity systemMessage = new ChatMessageEntity();
                                systemMessage.setSender("시스템");
                                systemMessage.setMessage(username + "님이 입장하셨습니다.");
                                systemMessage.setTimestamp(LocalDateTime.now());
                                systemMessage.setEventId(eventId);

                                getMessagingTemplate().convertAndSend(
                                        "/topic/chatroom/" + eventId,
                                        systemMessage
                                );
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }).start();
                    } else {
                        System.out.println("JWT 만료됨");
                    }
                } catch (Exception e) {
                    System.out.println("JWT 처리 오류 발생: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }

        return message;
    }
}