package ureca.ureca_mini.winner.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
@RequiredArgsConstructor
public class WinnerJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    public void entryBulkInsert(Set<String> users, int eventId) {
        StringBuilder sqlBuilder = new StringBuilder("INSERT INTO winner VALUES ");
        for (String userId : users) {
            sqlBuilder.append("(").append(userId).append(", ").append(eventId).append("), ");
        }
        sqlBuilder.deleteCharAt(sqlBuilder.length() - 1);
        sqlBuilder.deleteCharAt(sqlBuilder.length() - 1);

        jdbcTemplate.update(sqlBuilder.toString());
    }
}
