package ureca.ureca_mini.chat;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.security.Principal;

@Controller
public class EventPageController {

    @GetMapping("/event/{eventId}")
    public String loadChatPage(@PathVariable("eventId") int eventId, Model model, Principal principal) {
        model.addAttribute("eventId", eventId);
        model.addAttribute("username", principal.getName());
        return "chat";
    }
}
