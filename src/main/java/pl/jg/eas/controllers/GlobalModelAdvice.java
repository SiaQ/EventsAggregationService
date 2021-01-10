package pl.jg.eas.controllers;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import pl.jg.eas.services.UserContextService;

@ControllerAdvice
public class GlobalModelAdvice {

    private final UserContextService userContextService;

    public GlobalModelAdvice(UserContextService userContextService) {
        this.userContextService = userContextService;
    }

    @ModelAttribute("loggedAs")
    public String getLoggedAs() {
        return userContextService.getCurrentlyLoggedUserEmail();
    }
}
