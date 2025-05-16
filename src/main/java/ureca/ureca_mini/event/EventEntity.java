package ureca.ureca_mini.event;

import jakarta.persistence.*;
import lombok.*;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@NoArgsConstructor
@Table(name = "event")
public class EventEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    private String title;

    @Column
    private String content;

    @Column
    private Timestamp createdAt;

    @Column
    private String imageUrl;

    @Column
    private LocalDateTime eventDate;
    
    @Column
    private LocalDateTime endDate;

    @Column
    private int maxWinner;


    public static EventEntity toEventEntity(int id, String title, String content, Timestamp createdAt, String imageUrl, LocalDateTime eventDate){
        EventEntity e = new EventEntity();

        e.id = id;
        e.title = title;
        e.content = content;
        e.createdAt = createdAt;
        e.imageUrl = imageUrl;
        e.eventDate = eventDate;


        return e;
    }
}
