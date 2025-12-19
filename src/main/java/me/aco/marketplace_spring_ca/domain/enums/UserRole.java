package me.aco.marketplace_spring_ca.domain.enums;

public enum UserRole {

    USER("User"), 
    ADMIN("Admin");

    private final String displayName;

    private UserRole(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
