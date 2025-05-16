package ureca.ureca_mini.winner;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@NoArgsConstructor
@Table(name = "winner")
public class WinnerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    private int userId;

    @Column
    private int eventId;

    @Column
    private LocalDateTime createdAt;

    public static WinnerEntity toWinnerEntity(int userId, int eventId){
        WinnerEntity e = new WinnerEntity();

        e.setUserId(userId);
        e.setEventId(eventId);

        return e;
    }
}
