package ureca.ureca_mini.chat;


import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;
import ureca.ureca_mini.user.jwt.JWTUtil;

@Component
@RequiredArgsConstructor
public class JwtChannelInterceptor implements ChannelInterceptor {

    private final JWTUtil jwtUtil;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            String token = accessor.getFirstNativeHeader("Authorization");
            System.out.println("token : " + token);

            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
                System.out.println("token1:" + token);

                try {
                    if (!jwtUtil.isExpired(token)) {
                        String username = jwtUtil.getUsername(token);
                        System.out.println("✅ WebSocket 연결된 사용자: " + username);
                        accessor.setUser(() -> username);
                    } else {
                        System.out.println("❌ JWT 만료됨");
                    }
                } catch (Exception e) {
                    System.out.println("❌ JWT 처리 중 오류 발생: " + e.getMessage());
                    e.printStackTrace(); // 더 자세한 로그 원하면 이거 추가
                }
            }
        }

        return message;
    }
}