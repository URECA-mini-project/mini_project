package ureca.ureca_mini.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

//view 반환
@Controller
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;

    //상세 이벤트 페이지 view
    @GetMapping("eventDetail/{eventId}")
    public String EventDetail(Model model, @PathVariable("eventId") int eventId) {

        model.addAttribute("event", eventService.eventDetail(eventId));

        return "eventDetail";
    }

    //이벤트 리스트 페이지 view
    @GetMapping("mainpage")
    public String eventList(Model model) {
        model.addAttribute("eventList", eventService.eventList());

        return "mainpage";
    }
}
