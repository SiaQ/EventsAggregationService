package pl.jg.eas.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import pl.jg.eas.dtos.NewEventForm;
import pl.jg.eas.services.EventService;
import pl.jg.eas.services.UserContextService;

import javax.validation.Valid;

@Controller
public class EventController {

    private final EventService eventService;
    private final UserContextService userContextService;

    public EventController(EventService eventService, UserContextService userContextService) {
        this.eventService = eventService;
        this.userContextService = userContextService;
    }

    @GetMapping("/add-event")
    public String addEventForm(Model model) {
        final NewEventForm newEventForm = new NewEventForm();

        model.addAttribute("newEventForm", newEventForm);
        model.addAttribute("loggedAs", userContextService.getCurrentlyLoggedUserEmail());

        return "addEventForm";
    }

    @PostMapping("/add-event")
    public String addEventSubmit(
            @ModelAttribute @Valid NewEventForm newEventForm,
            BindingResult bindingResult,
            Model model
    ) {
        if(bindingResult.hasErrors()) {
            return "addEventForm";
        }

        model.addAttribute("loggedAs", userContextService.getCurrentlyLoggedUserEmail());
        eventService.addEvent(newEventForm);

        return "eventAddedView";
    }
}
