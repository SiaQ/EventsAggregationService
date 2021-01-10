package pl.jg.eas.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import pl.jg.eas.services.EventService;

@Controller
public class HomePageController {

    private final EventService eventService;

    public HomePageController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping("/")
    public String showHomePage(Model model) {
        model.addAttribute("events", eventService.getCurrentAndFutureEvents());
        return "homePage";
    }
}
