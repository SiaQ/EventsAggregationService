package pl.jg.eas.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import pl.jg.eas.services.EventService;
import pl.jg.eas.services.UserContextService;

@Controller
public class HomePageController {

    private final UserContextService userContextService;
    private final EventService eventService;

    public HomePageController(UserContextService userContextService, EventService eventService) {
        this.userContextService = userContextService;
        this.eventService = eventService;
    }

    @GetMapping("/")
    public String showHomePage(Model model) {
        model.addAttribute("loggedAs", userContextService.getCurrentlyLoggedUserEmail());
        model.addAttribute("events", eventService.getCurrentAndFutureEvents());
        return "homePage";
    }
}
