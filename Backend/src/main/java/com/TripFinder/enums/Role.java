package com.TripFinder.enums;

/**
 * Enum representing user roles in the TripFinder application.
 * Used for authorization and access control.
 */
public enum Role {
    USER("ROLE_USER"),
    ADMIN("ROLE_ADMIN");

    private final String authority;

    Role(String authority) {
        this.authority = authority;
    }

    public String getAuthority() {
        return authority;
    }

    @Override
    public String toString() {
        return authority;
    }
}