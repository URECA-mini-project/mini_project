package ureca.ureca_mini.winner.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;
import ureca.ureca_mini.winner.EntryRequest;
import ureca.ureca_mini.winner.WinnerRepository;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class EntryRedisControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EntryRedisService entryRedisService;

    @Autowired
    private WinnerRepository winnerRepository;

    @Autowired
    private WinnerRedisRepository winnerRedisRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @AfterEach
    public void afterEach() {
        winnerRedisRepository.deleteByKey("1");
        winnerRepository.deleteAll();
    }

    @Test
    @DisplayName("v1 - 정상적인 요청에 대한 응답으로 true가 반환됩니다")
    public void shouldReturnTrueWhenValidRequestGivenV1() throws Exception {
        // given
        EntryRequest request = new EntryRequest(1, 1);

        // when
        mockMvc.perform(MockMvcRequestBuilders.post("/api/entry/redis/v1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true));

        // then
        Assertions.assertNotNull(winnerRepository.findById(request.getUserId()));
    }

    @Test
    @DisplayName("v1 - 비정상적인 요청에 대한 응답으로 flase가 반환됩니다")
    public void shouldReturnFalseWhenValidRequestGivenV1() throws Exception {
        // given
        for (int i=1; i<=100; i++) {
            entryRedisService.entryV1(new EntryRequest(i, 1));
        }
        EntryRequest request = new EntryRequest(1, 1); // 중복 요청

        // when
        mockMvc.perform(MockMvcRequestBuilders.post("/api/entry/redis/v1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false));
    }
}
