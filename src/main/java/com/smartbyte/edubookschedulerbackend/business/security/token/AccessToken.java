package com.smartbyte.edubookschedulerbackend.business.security.token;

import com.smartbyte.edubookschedulerbackend.domain.Role;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public interface AccessToken {

    Long getId();

    Role getRole();

    Date getExpiration();

    boolean hasRole(String roleName);
}
