package ureca.ureca_mini.winner.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ureca.ureca_mini.winner.EntryRequest;
import ureca.ureca_mini.winner.EntryResponse;

import java.util.HashMap;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/entry/kafka")
public class ApplyKafkaController {

    private final WinnerService winnerService;

    //
    @PostMapping("/v1")
    public HashMap<String, Object> entryV1() {
        HashMap<String, Object> response = new HashMap<>();

        if(winnerService.apply(1, 1)) response.put("result", true);
        else response.put("result", false);

        return response;
    }

    @PostMapping("/temp")
    public ResponseEntity<EntryResponse> temp() {
        return ResponseEntity.ok(new EntryResponse(true));
    }

}
