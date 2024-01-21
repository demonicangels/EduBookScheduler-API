package com.smartbyte.edubookschedulerbackend.business.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class GetAssignedUserResponse {
    private long id;
    private String name;
    private String profilePicUrl;
}
