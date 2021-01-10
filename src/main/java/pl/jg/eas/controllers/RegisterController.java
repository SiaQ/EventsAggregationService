package pl.jg.eas.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import pl.jg.eas.dtos.NewUserForm;
import pl.jg.eas.services.UserService;

import javax.validation.Valid;

@Controller
public class RegisterController {

    private final UserService userService;

    public RegisterController(UserService userService) {
        this.userService = userService;
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
}
