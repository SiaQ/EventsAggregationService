package pl.jg.eas.exceptions;

public class RoleDoesntExistException extends RuntimeException {

    private String roleName;

    public RoleDoesntExistException(String roleName) {
        this.roleName = roleName;
    }

    @Override
    public String getMessage() {
        return String.format("Role identified by %s doesn't exist.", roleName);
    }

    public String getRoleName() {
        return roleName;
    }

    @Override
    public String toString() {
        return "RoleDoesntExistException{" +
                "roleName='" + roleName + '\'' +
                '}';
    }
}
