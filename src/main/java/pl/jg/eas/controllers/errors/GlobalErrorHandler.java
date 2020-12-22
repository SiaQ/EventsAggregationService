package pl.jg.eas.controllers.errors;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import pl.jg.eas.exceptions.UserWithSuchEmailExistsException;

@ControllerAdvice
public class GlobalErrorHandler {

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(UserWithSuchEmailExistsException.class)
    private String handle(UserWithSuchEmailExistsException e) {

        return "userAlreadyExist";
    }
}
