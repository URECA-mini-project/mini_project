package ureca.ureca_mini.winner.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ureca.ureca_mini.winner.WinnerEntity;
import ureca.ureca_mini.winner.WinnerRepository;


@Component
@RequiredArgsConstructor
public class ApplyConsumer {

    private final WinnerRepository winnerRepository;

    @KafkaListener(topics = "coupon_created", groupId = "coupon")
    public void listener(int userId) {
        WinnerEntity winner = WinnerEntity.toWinnerEntity(userId, 1);
        winnerRepository.save(winner);
    }
}
