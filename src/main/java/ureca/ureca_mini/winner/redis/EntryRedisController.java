package ureca.ureca_mini.winner.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ureca.ureca_mini.winner.EntryRequest;

@Slf4j
@RestController
@RequestMapping("/entry/redis")
@RequiredArgsConstructor
public class EntryRedisController {

    private final EntryRedisService entryRedisService;

    @PostMapping
    public void applyWinnerV1(@RequestBody EntryRequest request) {
        entryRedisService.entry(request);
    }
}
