package com.smartbyte.edubookschedulerbackend.business.security.token;

public interface AccessTokenEncoder {
    String generateJWT(AccessToken accessToken);
}


