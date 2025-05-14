package ureca.ureca_mini.chat.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.security.Principal;

@Controller
public class EventPageController {

    @GetMapping("/event/{eventId}")
    public String loadChatPage() {
        return "chat";
    }
}
