package ureca.ureca_mini.user.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ureca.ureca_mini.user.dto.JoinDTO;
import ureca.ureca_mini.user.repository.UserRepository;

@Component
@RequiredArgsConstructor
public class DefaultUserValidator implements UserValidator {
    private final UserRepository userRepository;

    @Override
    public void validate(JoinDTO dto) {
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
        if (dto.getPassword().length() < 8) {
            throw new IllegalArgumentException("비밀번호는 최소 8자 이상이어야 합니다.");
        }

    }
}
