package ureca.ureca_mini.user.service;

import ureca.ureca_mini.user.dto.JoinDTO;
import ureca.ureca_mini.user.entity.UserEntity;

// 회원 관련 비즈니스 로직 인터페이스

public interface JoinService {

    boolean isEmailDuplicate(String email);


    UserEntity join(JoinDTO dto);

}
