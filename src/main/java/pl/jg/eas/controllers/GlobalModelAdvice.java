package pl.jg.eas.controllers;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import pl.jg.eas.dtos.SearchOption;
import pl.jg.eas.enums.PeriodCriteria;
import pl.jg.eas.services.UserContextService;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
        return Arrays.stream(PeriodCriteria.values())
                .map(periodCriteria -> new SearchOption(periodCriteria.name(), periodCriteria.getCriteriaName()))
                .collect(Collectors.toList());
    }

    @ModelAttribute("periodCriteria")
    public PeriodCriteria periodCriteria() {
        return PeriodCriteria.FUTURE;
    }
}
