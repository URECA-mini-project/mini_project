package ureca.ureca_mini.chat;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class EventPageController {

    @GetMapping("/event/{eventId}")
    public String loadChatPage(@PathVariable("eventId") int eventId, Model model) {
        model.addAttribute("eventId", eventId);
        return "chat";
    }
}
