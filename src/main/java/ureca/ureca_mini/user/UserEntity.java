package ureca.ureca_mini.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.sql.Date;
import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@NoArgsConstructor
@Table(name = "event")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    private String email;

    @Column
    private String username;

    @Column
    private String password;

    @Column
    private LocalDateTime createdAt;

    @Column
    private String phoneNumber;

    @Column
    private Date birthday;
}
