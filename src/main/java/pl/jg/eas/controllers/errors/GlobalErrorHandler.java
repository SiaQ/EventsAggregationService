package pl.jg.eas.controllers.errors;

import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import pl.jg.eas.exceptions.EventDoesntExistException;
import pl.jg.eas.exceptions.UserDoesntExistException;
import pl.jg.eas.exceptions.UserWithSuchEmailExistsException;
import pl.jg.eas.services.UserContextService;

@ControllerAdvice
public class GlobalErrorHandler {

    private final UserContextService userContextService;

    public GlobalErrorHandler(UserContextService userContextService) {
        this.userContextService = userContextService;
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(UserWithSuchEmailExistsException.class)
    private String handle(UserWithSuchEmailExistsException e) {

        return "user/userAlreadyExist";
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(UserDoesntExistException.class)
    private String handle(UserDoesntExistException e) {

        return "user/userDoesntExist";
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(EventDoesntExistException.class)
    private String handle(EventDoesntExistException e, Model model) {

        model.addAttribute("loggedAs", userContextService.getCurrentlyLoggedUserEmail());

        return "event/eventDoesntExist";
    }

    @ExceptionHandler(Exception.class)
    private String handle(Exception e) {
        return "errors/unidentifiedException";
    }
}
