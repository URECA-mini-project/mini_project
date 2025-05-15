package ureca.ureca_mini.winner.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ureca.ureca_mini.winner.EntryRequest;
import ureca.ureca_mini.winner.EntryResponse;

@Slf4j
@RestController
@RequestMapping("/api/entry/redis")
@RequiredArgsConstructor
public class EntryRedisController {

    private final EntryRedisService entryRedisService;

    @PostMapping("/v1")
    public ResponseEntity<EntryResponse> entryV1(@RequestBody EntryRequest request) {
        boolean isSuccess = entryRedisService.entryV1(request);
        return ResponseEntity.ok(new EntryResponse(isSuccess));
    }

    @PostMapping("/v2")
    public ResponseEntity<EntryResponse> entryV2(@RequestBody EntryRequest request) {
        boolean isSuccess = entryRedisService.entryV2(request);
        return ResponseEntity.ok(new EntryResponse(isSuccess));
    }
}
