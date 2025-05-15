package ureca.ureca_mini.user.service;

import ureca.ureca_mini.user.dto.JoinDTO;
import ureca.ureca_mini.user.entity.UserEntity;

// 회원 관련 비즈니스 로직 인터페이스

public interface JoinService {

    boolean isEmailDuplicate(String email);

    // 회원가입 처리 후 저장된 UserEntity 반환
    UserEntity join(JoinDTO dto);

}
