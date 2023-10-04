package com.smartbyte.edubookschedulerbackend.business.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class LoginResponse {
    private long id;
    private String name;
}
