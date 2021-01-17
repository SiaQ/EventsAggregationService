package pl.jg.eas.enums;

public enum Roles {
    COMMON_USER("ROLE_COMMON_USER"),
    EVENT_MANAGER("ROLE_EVENT_MANAGER"),
    ADMIN("ROLE_ADMIN");

    String roleName;

    Roles(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleName() {
        return roleName;
    }
}
