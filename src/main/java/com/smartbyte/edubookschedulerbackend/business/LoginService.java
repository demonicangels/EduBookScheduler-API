package com.smartbyte.edubookschedulerbackend.business;

import com.smartbyte.edubookschedulerbackend.business.request.LoginRequest;
import com.smartbyte.edubookschedulerbackend.business.response.LoginResponse;

public interface LoginService {
    LoginResponse Login(LoginRequest request);
}
