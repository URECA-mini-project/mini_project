package ureca.ureca_mini.event;

import jakarta.persistence.*;
import lombok.*;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.sql.Timestamp;

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


    public static EventEntity toEventEntity(int id, String title, String content, Timestamp createdAt, String imageUrl){
        EventEntity e = new EventEntity();

        e.id = id;
        e.title = title;
        e.content = content;
        e.createdAt = createdAt;
        e.imageUrl = imageUrl;

        return e;
    }
}
