package ureca.ureca_mini.chat.util;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ureca.ureca_mini.chat.entity.ForbiddenwordEntity;
import ureca.ureca_mini.chat.repository.ForbiddenWordRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ForbiddenWordSeeder {

    private final ForbiddenWordRepository forbiddenWordRepository;

    @PostConstruct
    public void seedForbiddenWords() {
        if (forbiddenWordRepository.count() == 0) {
            List<String> words = List.of(
                    "바보", "멍청이", "씨발", "fuck", "shit", "idiot", "병신", "개새끼", "damn", "꺼져","시발"
            );
            words.forEach(word ->
                    forbiddenWordRepository.save(ForbiddenwordEntity.builder().word(word).build())
            );
            System.out.println("테스트 금칙어 데이터 삽입");
        }
    }
}