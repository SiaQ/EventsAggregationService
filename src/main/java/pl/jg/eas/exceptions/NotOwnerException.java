package pl.jg.eas.exceptions;

public class NotOwnerException extends RuntimeException {
    public NotOwnerException(String message) {
        super(message);
    }
}
