package com.smartbyte.edubookschedulerbackend.business;

import com.smartbyte.edubookschedulerbackend.business.response.JWTResponse;
import com.smartbyte.edubookschedulerbackend.business.request.LoginRequest;

public interface AuthenticationService {
    JWTResponse loginUser(LoginRequest loginRequest);
    Boolean authenticateUser(Long userId);
}
