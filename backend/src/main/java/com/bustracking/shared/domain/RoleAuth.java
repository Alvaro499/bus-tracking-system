package com.bustracking.shared.domain;

public enum RoleAuth {
    DRIVER,
    ADMIN_COMPANY,
    ADMIN_SYSTEM;

    public String getAuthority() {
        return "ROLE_" + this.name();
    }
}