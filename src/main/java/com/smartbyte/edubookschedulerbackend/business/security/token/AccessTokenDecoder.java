package com.smartbyte.edubookschedulerbackend.business.security.token;

public interface AccessTokenDecoder {
    AccessToken decode(String accessTokenEncoded);
}
