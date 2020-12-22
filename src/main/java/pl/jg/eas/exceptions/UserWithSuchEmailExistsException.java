package pl.jg.eas.exceptions;

public class UserWithSuchEmailExistsException extends RuntimeException {

    private String email;

    public UserWithSuchEmailExistsException(String email) {
        this.email = email;
    }

    @Override
    public String getMessage() {
        return String.format("User identified by %s already exists!", email);
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String toString() {
        return "UserWithSuchEmailExistsException{" +
                "email='" + email + '\'' +
                '}';
    }
}
