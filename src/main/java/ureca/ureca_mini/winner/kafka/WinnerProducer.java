package ureca.ureca_mini.winner.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WinnerProducer {
    private final KafkaTemplate<String, Integer> kafkaTemplate;

    public void create(int userId) {
        kafkaTemplate.send("coupon_created", userId);
    }
}
