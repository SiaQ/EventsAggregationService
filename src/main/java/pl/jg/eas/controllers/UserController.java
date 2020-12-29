package pl.jg.eas.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import pl.jg.eas.dtos.EditUserForm;
import pl.jg.eas.dtos.NewUserForm;
import pl.jg.eas.services.UserContextService;
import pl.jg.eas.services.UserService;

import javax.validation.Valid;

@Controller
public class UserController {

    private final UserService userService;
    private final UserContextService userContextService;

    public UserController(UserService userService, UserContextService userContextService) {
        this.userService = userService;
        this.userContextService = userContextService;
    }

    @GetMapping("/user-register")
    public String userRegisterForm(Model model) {
        final NewUserForm newUserForm = new NewUserForm();

        model.addAttribute("newUserForm", newUserForm);

        return "user/userRegisterForm";
    }

    @PostMapping("/user-register")
    public String userRegisterSubmit(
            @ModelAttribute @Valid NewUserForm newUserForm,
            BindingResult bindingResult
    ) {

        if (bindingResult.hasErrors()) {
            return "user/userRegisterForm";
        }

        userService.registerUser(newUserForm);

        return "user/userRegisterThankYouPage";
    }

    @GetMapping("/options")
    public String editUserForm(Model model) {
        final EditUserForm editUserForm = new EditUserForm();

        model.addAttribute("loggedAs", userContextService.getCurrentlyLoggedUserEmail());
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
}
