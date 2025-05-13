package ureca.ureca_mini.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ureca.ureca_mini.user.dto.JoinDTO;
import ureca.ureca_mini.user.entity.UserEntity;
import ureca.ureca_mini.user.repository.UserRepository;
import ureca.ureca_mini.user.validator.UserValidator;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JoinServiceImpl implements JoinService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;    // ① 추가
    private final List<UserValidator> validators;

    /** 이메일·유저네임 중복 검사 */
    @Override
    public boolean isDuplicate(JoinDTO dto) {
        return userRepository.existsByEmail(dto.getEmail())
                || userRepository.existsByUsername(dto.getUsername());
    }

    /** 회원가입 처리 */
    @Override
    public UserEntity join(JoinDTO dto) {
        // 1) 검증
        validators.forEach(v -> v.validate(dto));

        // 2) DTO → Entity 변환
        UserEntity user = UserEntity.builder()
                .email(dto.getEmail())
                .username(dto.getUsername())
                .password(passwordEncoder.encode(dto.getPassword()))
                .phoneNumber(dto.getPhoneNumber())
                .birthday(dto.getBirthday())
                .build();

        // 3) 저장 및 반환
        return userRepository.save(user);
    }
}
