package ureca.ureca_mini.user.service;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ureca.ureca_mini.user.dto.JoinDTO;
import ureca.ureca_mini.user.entity.UserEntity;
import ureca.ureca_mini.user.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
class JoinServiceTest {

    @Autowired
    JoinService joinService;

    @Autowired
    UserRepository userRepository;

    @Test
    void 회원가입_정상_동작() {
        JoinDTO dto = new JoinDTO();
        dto.setEmail("test@email.com");
        dto.setUsername("tester");
        dto.setPassword("12345678");

        UserEntity saved = joinService.join(dto);

        assertNotNull(userRepository.findByUsername("tester"));
    }

    @Test
    void 중복_이메일_가입_불가() {
        JoinDTO dto1 = new JoinDTO();
        dto1.setEmail("test@email.com");
        dto1.setUsername("user1");
        dto1.setPassword("12345678");
        joinService.join(dto1);

        JoinDTO dto2 = new JoinDTO();
        dto2.setEmail("test@email.com");
        dto2.setUsername("user2");
        dto2.setPassword("12345678");

        boolean duplicate = joinService.isEmailDuplicate(dto2.getEmail());
        assertTrue(duplicate);
    }
}
