package com.smartbyte.edubookschedulerbackend.business.security.token;

import com.smartbyte.edubookschedulerbackend.domain.Role;

import java.util.Date;

public interface AccessToken {

    Long getId();

    Role getRole();

    Date getExpiration();

    boolean hasRole(String roleName);
}
