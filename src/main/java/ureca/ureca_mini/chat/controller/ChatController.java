package ureca.ureca_mini.chat.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ureca.ureca_mini.chat.entity.ChatMessageEntity;
import ureca.ureca_mini.chat.redis.RedisPublisher;
import ureca.ureca_mini.chat.service.ChatRedisService;
import ureca.ureca_mini.chat.service.FilterService;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatRedisService redisService;
    private final RedisPublisher redisPublisher;
    private final FilterService filterService;

    @MessageMapping("/chat.sendMessage/{eventId}")
    public void sendMessage(@Payload ChatMessageEntity message,
                                         @DestinationVariable("eventId") Integer eventId,
                                         Principal principal) {
        String userId = "null";
        try {

            if (principal instanceof Authentication auth && auth.getName() != null) {
                userId = auth.getName();
            } else if (principal != null) {
                userId = principal.getName();
            }

            message.setSender(userId);
            message.setEventId(eventId);;
            message.setTimestamp(LocalDateTime.now());

            //도배 여부 검사
            if (redisService.isSpam(userId, 5, 10)) {
                System.out.println("도배로 간주되어 메시지 차단됨 - userId: " + userId);

                // 도배 경고 메시지 생성
                ChatMessageEntity warning = new ChatMessageEntity();
                warning.setSender("시스템");
                warning.setMessage("도배 감지: 10초 동안 5회 이상 메시지를 보냈습니다.");
                warning.setEventId(eventId);
                warning.setTimestamp(LocalDateTime.now());

                // 개인 사용자 큐로 전송
                messagingTemplate.convertAndSendToUser(
                        principal.getName(), "/queue/warning", warning
                );

                return;
            }

            //도배 시간 기록
            redisService.recordUserMessageTime(userId);

            //비속어 마스킹 처리
            String rawMsg = message.getMessage();
            String maskedMsg = filterService.maskForbiddenWords(rawMsg);
            message.setMessage(maskedMsg);

            redisService.saveMessage(eventId, message);
            redisPublisher.publish("CHAT: "+eventId, message);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
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

