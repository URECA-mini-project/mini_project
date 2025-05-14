package ureca.ureca_mini.chat.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ureca.ureca_mini.chat.entity.ForbiddenwordEntity;
import ureca.ureca_mini.chat.repository.ForbiddenWordRepository;

import java.util.List;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class FilterService {

    private final ForbiddenWordRepository forbiddenWordRepository;

    //마스킹 처리
    public String maskForbiddenWords(String message) {
        List<ForbiddenwordEntity> forbiddenWords = forbiddenWordRepository.findAll();
        String masked = message;

        for (ForbiddenwordEntity fw : forbiddenWords) {
            String word = fw.getWord();
            String regex = "(?i)" + Pattern.quote(word);
            masked = masked.replaceAll(regex, "*".repeat(word.length()));
        }
        return masked;
    }
}
