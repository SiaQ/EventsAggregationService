package pl.jg.eas.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pl.jg.eas.dtos.EditEventForm;
import pl.jg.eas.dtos.EventInfoDto;
import pl.jg.eas.dtos.NewCommentForm;
import pl.jg.eas.dtos.NewEventForm;
import pl.jg.eas.services.EventService;
import pl.jg.eas.services.UserContextService;

import javax.validation.Valid;
import java.util.Optional;

@Controller
public class EventController {

    private final EventService eventService;
    private final UserContextService userContextService;
    private static final String loggedAs = "loggedAs";
    private static final String redirectEvents = "redirect:/events/";

    public EventController(EventService eventService, UserContextService userContextService) {
        this.eventService = eventService;
        this.userContextService = userContextService;
    }

    @GetMapping("/add-event")
    public String addEventForm(Model model) {
        final NewEventForm newEventForm = new NewEventForm();

        model.addAttribute(loggedAs, userContextService.getCurrentlyLoggedUserEmail());
        model.addAttribute("newEventForm", newEventForm);

        return "event/addEventForm";
    }

    @PostMapping("/add-event")
    public String addEventSubmit(
            @ModelAttribute @Valid NewEventForm newEventForm,
            BindingResult bindingResult,
            Model model
    ) {
        if(bindingResult.hasErrors()) {
            model.addAttribute(loggedAs, userContextService.getCurrentlyLoggedUserEmail());
            return "event/addEventForm";
        }

        model.addAttribute(loggedAs, userContextService.getCurrentlyLoggedUserEmail());
        eventService.addEvent(newEventForm);

        return "event/eventAddedView";
    }

    @GetMapping("/search-events")
    public String searchEvents(
            @RequestParam String title,
            @RequestParam String time,
            Model model
    ) {
        model.addAttribute(loggedAs, userContextService.getCurrentlyLoggedUserEmail());
        model.addAttribute("foundEvents", eventService.getEventsContaining(title, time));

        return "event/foundEventsView";
    }

    @GetMapping("/events/{eventId}")
    public String getSingleEventInfo(
            @PathVariable Long eventId,
            Model model
    ) {
        final String currentlyLoggedUser = userContextService.getCurrentlyLoggedUserEmail();
        final NewCommentForm newCommentForm = new NewCommentForm();
        model.addAttribute(loggedAs, currentlyLoggedUser);
        model.addAttribute("isOwnerOrAdmin", eventService.isOwnerOrAdmin(eventId, currentlyLoggedUser));
        model.addAttribute("newCommentForm", newCommentForm);
        model.addAttribute("isSignedUpFor", eventService.isSignedUp(eventId, userContextService.getCurrentlyLoggedUserEmail()));
        model.addAttribute("usersSignedUpFor", eventService.getSignedUpUsers(eventId));

        final Optional<EventInfoDto> singleEventInfo = eventService.getSingleEventInfo(eventId);

        if(singleEventInfo.isEmpty()) {
            model.addAttribute(loggedAs, userContextService.getCurrentlyLoggedUserEmail());
            return "event/eventDoesntExist";
        }

        model.addAttribute("event", singleEventInfo.get());

        return "event/singleEventView";
    }

    @GetMapping("/events/{eventId}/edit")
    public String editEvent(
            @PathVariable Long eventId,
            Model model
    ) {
        final EditEventForm editEventForm = new EditEventForm();

        model.addAttribute(loggedAs, userContextService.getCurrentlyLoggedUserEmail());
        model.addAttribute("editEventForm", editEventForm);

        final Optional<EventInfoDto> singleEventInfo = eventService.getSingleEventInfo(eventId);

        if(singleEventInfo.isEmpty()) {
            model.addAttribute(loggedAs, userContextService.getCurrentlyLoggedUserEmail());
            return "event/eventDoesntExist";
        }

        model.addAttribute("eventInfo", singleEventInfo.get());

        return "event/editEventForm";
    }

    @PostMapping("/events/{eventId}/edit")
    public String editEventSubmit(
            @PathVariable Long eventId,
            @ModelAttribute @Valid EditEventForm editEventForm,
            BindingResult bindingResult,
            Model model
    ) {

        if(bindingResult.hasErrors()) {
            model.addAttribute(loggedAs, userContextService.getCurrentlyLoggedUserEmail());
            return "event/editEventForm";
        }

        model.addAttribute(loggedAs, userContextService.getCurrentlyLoggedUserEmail());
        eventService.editEvent(editEventForm, eventId);

        return redirectEvents + eventId;
    }

    @PostMapping("/events/{eventId}/comment/add")
    public String addCommentToEventForm(
            @PathVariable Long eventId,
            @ModelAttribute @Valid NewCommentForm newCommentForm,
            BindingResult bindingResult,
            Model model
            ) {

        if(bindingResult.hasErrors()) {
            model.addAttribute(loggedAs, userContextService.getCurrentlyLoggedUserEmail());
            return redirectEvents + eventId;
        }

        model.addAttribute(loggedAs, userContextService.getCurrentlyLoggedUserEmail());
        eventService.addNewComment(eventId, newCommentForm, userContextService.getCurrentlyLoggedUserEmail());

        return redirectEvents + eventId;
    }

    @PostMapping("/events/{eventId}/sign-up")
    public String signUpForEvent(
            @PathVariable Long eventId,
            Model model
    ) {
        model.addAttribute(loggedAs, userContextService.getCurrentlyLoggedUserEmail());
        eventService.signUp(eventId, userContextService.getCurrentlyLoggedUserEmail());

        return redirectEvents + eventId;
    }

    @PostMapping("/events/{eventId}/sign-off")
    public String signOffFromEvent(
            @PathVariable Long eventId,
            Model model
    ) {
        model.addAttribute(loggedAs, userContextService.getCurrentlyLoggedUserEmail());
        eventService.signOff(eventId, userContextService.getCurrentlyLoggedUserEmail());

        return redirectEvents + eventId;
    }
}
