package ureca.ureca_mini.winner.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ureca.ureca_mini.event.EventService;
import ureca.ureca_mini.winner.EntryRequest;
import ureca.ureca_mini.winner.EntryResponse;
import ureca.ureca_mini.winner.WinnerEntity;
import ureca.ureca_mini.winner.WinnerRepository;

import java.util.HashMap;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/entry/kafka")
public class ApplyKafkaController {
    private final EventService eventService;
    private final WinnerService winnerService;
    private final WinnerRepository winnerRepository;

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

    @PostMapping("/winnertemp")
    public ResponseEntity<EntryResponse> winnertemp() {
        if(winnerService.winnerCount() == 0) return ResponseEntity.ok(new EntryResponse(true));
        else return ResponseEntity.ok(new EntryResponse(false));
    }

    @PostMapping("/savewinnertemp")
    public ResponseEntity<EntryResponse> savewinnertemp() {
        WinnerEntity w = WinnerEntity.toWinnerEntity(1, 1);
        winnerRepository.save(w);
        return ResponseEntity.ok(new EntryResponse(true));
    }

    @PostMapping("/mysqltest")
    public ResponseEntity<EntryResponse> mysqltest() {

        if(eventService.eventDetail(1).getId() == 1) return ResponseEntity.ok(new EntryResponse(true));
        else return ResponseEntity.ok(new EntryResponse(false));
    }

}
