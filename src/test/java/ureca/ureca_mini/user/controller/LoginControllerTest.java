package ureca.ureca_mini.user.controller;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import ureca.ureca_mini.user.entity.UserEntity;
import ureca.ureca_mini.user.repository.UserRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;

@AutoConfigureMockMvc
@SpringBootTest
@Transactional
class LoginControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        // 회원가입된 유저를 DB에 미리 저장
        userRepository.save(UserEntity.builder()
                .email("login@test.com")
                .username("loginuser")
                .password(passwordEncoder.encode("testpassword")) // 인코딩된 비밀번호
                .build());
    }

    @Test
    void 로그인_성공_시_JWT_발급됨() throws Exception {
        String json = """
                {
                    "username": "loginuser",
                    "password": "testpassword"
                }
                """;

        mockMvc.perform(post("/api/auth/login") // LoginFilter URL
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(header().exists("Authorization"));
    }

    @Test
    void 로그인_실패_비밀번호_불일치() throws Exception {
        String json = """
                {
                    "username": "loginuser",
                    "password": "wrongpass"
                }
                """;

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void 로그인_실패_존재하지_않는_유저() throws Exception {
        String json = """
                {
                    "username": "nouser",
                    "password": "anypass"
                }
                """;

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isUnauthorized());
    }
}

