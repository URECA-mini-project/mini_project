package ureca.ureca_mini.chat.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageEntity {
    private int eventId;
    private String sender;
    private String message;
    private LocalDateTime timestamp;
}
