package pl.jg.eas.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Slf4j
public class LoginController {

    @GetMapping("/user-login")
    public String showLoginForm() {
        return "user/userLoginForm";
    }

    @GetMapping("/user-login-error")
    public String invalidCredentials() {
        return "user/invalidCredentials";
    }
}
