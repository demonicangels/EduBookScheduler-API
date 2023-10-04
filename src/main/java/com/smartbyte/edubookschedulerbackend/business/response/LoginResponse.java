package com.smartbyte.edubookschedulerbackend.business.response;

import lombok.AllArgsConstructor;
import lombok.Builder;

@AllArgsConstructor
@Builder
public class LoginResponse {
    private long id;
    private String name;
}
