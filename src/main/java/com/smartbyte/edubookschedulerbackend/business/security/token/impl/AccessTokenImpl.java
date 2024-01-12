package com.smartbyte.edubookschedulerbackend.business.security.token.impl;

import com.smartbyte.edubookschedulerbackend.business.security.token.AccessToken;
import com.smartbyte.edubookschedulerbackend.domain.Role;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@EqualsAndHashCode
@Getter
@Setter
@Builder
public class AccessTokenImpl implements AccessToken {

    private final Long userId;
    private final Role role;
    private final Date expiration;

    @Override
    public Long getId() {
        return userId;
    }

    @Override
    public Date getExpiration() {
        return this.expiration;
    }

    @Override
    public boolean hasRole(String roleName) {
        return this.role.name().equals(roleName);
    }
}
