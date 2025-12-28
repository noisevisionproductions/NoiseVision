package org.noisevisionproductions.noisevision.auth.model.enums;

import java.util.HashSet;
import java.util.Set;

public enum Role {
    USER,
    ADMIN;

    public Set<Authority> getAuthorities() {
        Set<Authority> authorities = new HashSet<>();

        authorities.add(Authority.SEND_MESSAGES);
        if (this == ADMIN) {
            authorities.add(Authority.CREATE_PROJECTS);
            authorities.add(Authority.EDIT_PROJECTS);
            authorities.add(Authority.DELETE_PROJECTS);
            authorities.add(Authority.ACCESS_KAFKA_DASHBOARD);
        }

        return authorities;
    }
}
