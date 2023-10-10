package com.smartbyte.edubookschedulerbackend.business.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class GetUserProfileRequest {
    private long id;
}
