package com.smartbyte.edubookschedulerbackend.business.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class LoginRequest {
    private String email;
    private String password;
}
