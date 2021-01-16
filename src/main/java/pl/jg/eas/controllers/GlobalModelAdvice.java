package pl.jg.eas.controllers;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import pl.jg.eas.dtos.SearchOption;
import pl.jg.eas.services.UserContextService;

import java.util.List;

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

    @ModelAttribute("searchOptions")
    public List<SearchOption> getSearchOptions() {
        return List.of(
                new SearchOption("Future"),
                new SearchOption("Current and future"),
                new SearchOption("All"));
    }
}
