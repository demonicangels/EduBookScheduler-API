package com.smartbyte.edubookschedulerbackend.business.response;

import com.smartbyte.edubookschedulerbackend.domain.Role;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.util.OptionalLong;

@Data
@Builder
@Getter
public class GetUserProfileResponse {
    private long id;
    private String name;
    private String email;
    private String profilePicURL;
    private Role role;
    private OptionalLong PCN;
}
