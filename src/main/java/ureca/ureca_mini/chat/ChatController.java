package ureca.ureca_mini.chat;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ureca.ureca_mini.user.repository.UserRepository;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatRedisService redisService;

    @MessageMapping("/chat.sendMessage/{eventId}")
    @SendTo("/topic/chatroom/{eventId}")
    public ChatMessageEntity sendMessage(@Payload ChatMessageEntity message,
                            @DestinationVariable("eventId") Integer eventId,
                            Principal principal) {
        String userId = "null";
        try {

            if (principal instanceof Authentication auth && auth.getName() != null) {
                userId = auth.getName();
            } else if (principal != null) {
                userId = principal.getName();
            }
            //System.out.println(userId); t

            message.setSender(userId);
            message.setEventId(eventId);;
            message.setTimestamp(LocalDateTime.now());
            //System.out.println(message); t

            redisService.saveMessage(eventId, message);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

        return message;
    }


    @MessageMapping("/chat.loadHistory/{eventId}")
    public void loadChatHistory(@DestinationVariable("eventId") Integer eventId, Principal principal) {

        List<ChatMessageEntity> messages = redisService.getMessageHistory(eventId);

        messagingTemplate.convertAndSendToUser(
                principal.getName(), "/queue/history", messages
        );
    }

    @GetMapping("/clear/{eventId}")
    public ResponseEntity<String> clearChat(@PathVariable("eventId") Integer eventId) {
        redisService.deleteChatHistory(eventId);
        return ResponseEntity.ok("Redis 채팅 기록 삭제 : " + eventId);
    }
}

