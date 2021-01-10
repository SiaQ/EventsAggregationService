package pl.jg.eas.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import pl.jg.eas.dtos.EditUserForm;
import pl.jg.eas.services.EventService;
import pl.jg.eas.services.UserContextService;
import pl.jg.eas.services.UserService;

import javax.validation.Valid;

@Controller
public class UserController {

    private final UserService userService;
    private final UserContextService userContextService;
    private final EventService eventService;

    public UserController(UserService userService, UserContextService userContextService, EventService eventService) {
        this.userService = userService;
        this.userContextService = userContextService;
        this.eventService = eventService;
    }

    @GetMapping("/options")
    public String editUserForm(Model model) {
        final EditUserForm editUserForm = new EditUserForm();

        model.addAttribute("editUserForm", editUserForm);

        return "user/userOptionsView";
    }

    @PostMapping("/options")
    public String editUserSubmit(
            @ModelAttribute @Valid EditUserForm editUserForm,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            return "user/userOptionsView";
        }

        userService.editUser(editUserForm, userContextService.getCurrentlyLoggedUserEmail());

        return "redirect:/";
    }

    @GetMapping("/user-events")
    public String getUserEvents(
            Model model
    ) {
        final String currentlyLoggedUserEmail = userContextService.getCurrentlyLoggedUserEmail();
        model.addAttribute("userOwnerEvents", eventService.getUserOwnerEvents(currentlyLoggedUserEmail));

        return "user/userEventsView";
    }
}
