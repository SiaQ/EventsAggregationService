package pl.jg.eas.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pl.jg.eas.dtos.EditEventForm;
import pl.jg.eas.dtos.NewCommentForm;
import pl.jg.eas.dtos.NewEventForm;
import pl.jg.eas.enums.PeriodCriteria;
import pl.jg.eas.services.EventService;
import pl.jg.eas.services.UserContextService;

import javax.validation.Valid;

@Controller
public class EventController {

    private static final String REDIRECT_EVENTS = "redirect:/events/";

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

        return "event/addEventForm";
    }

    @PostMapping("/add-event")
    public String addEventSubmit(
            @ModelAttribute @Valid NewEventForm newEventForm,
            BindingResult bindingResult
    ) {

        if (bindingResult.hasErrors()) {
            return "event/addEventForm";
        }

        eventService.addEvent(newEventForm);

        return "event/eventAddedView";
    }

    @GetMapping("/search-events")
    public String searchEvents(
            @RequestParam String title,
            @RequestParam PeriodCriteria periodCriteria,
            Model model
    ) {
        model.addAttribute("title", title);
        model.addAttribute("periodCriteria", periodCriteria);
        model.addAttribute("foundEvents", eventService.getEventsContaining(title, periodCriteria));

        return "event/foundEventsView";
    }

    @GetMapping("/events/{eventId}")
    public String getSingleEventInfo(
            @PathVariable Long eventId,
            Model model
    ) {
        final String currentlyLoggedUser = userContextService.getCurrentlyLoggedUserEmail();
        final NewCommentForm newCommentForm = new NewCommentForm();

        model.addAttribute("isOwnerOrAdmin", eventService.isOwnerOrAdmin(
                eventId, currentlyLoggedUser));
        model.addAttribute("newCommentForm", newCommentForm);
        model.addAttribute("isSignedUpFor", eventService.isSignedUp(
                eventId, userContextService.getCurrentlyLoggedUserEmail()));
        model.addAttribute("usersSignedUpFor", eventService.getSignedUpUsers(eventId));
        model.addAttribute("event", eventService.getSingleEventInfo(eventId));

        return "event/singleEventView";
    }

    @GetMapping("/events/{eventId}/edit")
    public String editEvent(
            @PathVariable Long eventId,
            Model model
    ) {
        final EditEventForm editEventForm = new EditEventForm();

        model.addAttribute("editEventForm", editEventForm);
        model.addAttribute("eventInfo", eventService.getSingleEventInfo(eventId));

        return "event/editEventForm";
    }

    @PostMapping("/events/{eventId}/edit")
    public String editEventSubmit(
            @PathVariable Long eventId,
            @ModelAttribute @Valid EditEventForm editEventForm,
            BindingResult bindingResult
    ) {

        if (bindingResult.hasErrors()) {
            return "event/editEventForm";
        }

        eventService.editEvent(editEventForm, eventId);

        return REDIRECT_EVENTS + eventId;
    }

    @PostMapping("/events/{eventId}/comment/add")
    public String addCommentToEventForm(
            @PathVariable Long eventId,
            @ModelAttribute @Valid NewCommentForm newCommentForm,
            BindingResult bindingResult
    ) {

        if (bindingResult.hasErrors()) {
            return REDIRECT_EVENTS + eventId;
        }

        eventService.addNewComment(eventId, newCommentForm, userContextService.getCurrentlyLoggedUserEmail());

        return REDIRECT_EVENTS + eventId;
    }

    @PostMapping("/events/{eventId}/sign-up")
    public String signUpForEvent(
            @PathVariable Long eventId
    ) {
        eventService.signUp(eventId, userContextService.getCurrentlyLoggedUserEmail());

        return REDIRECT_EVENTS + eventId;
    }

    @PostMapping("/events/{eventId}/sign-off")
    public String signOffFromEvent(
            @PathVariable Long eventId
    ) {
        eventService.signOff(eventId, userContextService.getCurrentlyLoggedUserEmail());

        return REDIRECT_EVENTS + eventId;
    }
}
