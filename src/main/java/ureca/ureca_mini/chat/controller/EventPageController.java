package ureca.ureca_mini.chat.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
public class EventPageController {

    @GetMapping("/event/{eventId}")
    public String loadChatPage() {
        return "chat";
    }

    @GetMapping("/main")
    public String mainP() {return "main";}
}
