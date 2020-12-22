package pl.jg.eas.exceptions;

public class UserDoesntExistException extends RuntimeException {

    private String email;

    public UserDoesntExistException(String email) {
        this.email = email;
    }

    @Override
    public String getMessage() {
        return String.format("User identified by %s doesn't exist.", email);
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String toString() {
        return "UserDoesntExistException{" +
                "email='" + email + '\'' +
                '}';
    }
}
