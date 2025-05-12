package ureca.ureca_mini.chat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageEntity {
    private int roomId;
    private String sender;
    private String message;
    private LocalDateTime timestamp;
}
