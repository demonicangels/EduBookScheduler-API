package com.smartbyte.edubookschedulerbackend.business.security.token.impl;

import com.smartbyte.edubookschedulerbackend.business.exception.UserNotFoundException;
import com.smartbyte.edubookschedulerbackend.business.security.token.AccessToken;
import com.smartbyte.edubookschedulerbackend.business.security.token.AccessTokenEncoder;
import com.smartbyte.edubookschedulerbackend.business.security.token.AccessTokenDecoder;
import com.smartbyte.edubookschedulerbackend.domain.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static java.lang.Long.parseLong;

@Service
public class AccessTokenDecoderEncoderImpl implements AccessTokenEncoder, AccessTokenDecoder {
    private final Key key;

    public AccessTokenDecoderEncoderImpl(@Value("${jwt.secret}") String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String extractId(AccessToken token) {
        return extractClaim(token.toString(), Claims::getSubject);
    }

    public Date extractExpiration(AccessToken token) {
        return extractClaim(token.toString(), Claims::getExpiration);
    }

    private  <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    @Override
    public String generateJWT(AccessToken accessToken) {
        Map<String, Object> claimsMap = new HashMap<>();

        if (accessToken.getRole() != null){
            claimsMap.put("role", accessToken.getRole().name());
        }
        if (accessToken.getId() != null) {
            claimsMap.put("userId", accessToken.getId());
        }

        Instant now = Instant.now();


        return Jwts.builder()
                .setSubject(accessToken.getId().toString())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plus(30, ChronoUnit.HOURS)))
                .addClaims(claimsMap)
                .signWith(key)
                .compact();
    }

    @Override
    public AccessToken decode(String accessTokenEncoded) {
        try {
            Jwt<?, Claims> jwt = Jwts.parserBuilder().setSigningKey(key).setAllowedClockSkewSeconds(60).build()
                    .parseClaimsJws(accessTokenEncoded);

            Claims claims = jwt.getBody();

            String strRole = claims.get("role").toString();
            Role role = Role.valueOf(strRole);

            Date expiration =  claims.getExpiration();

            return AccessTokenImpl.builder()
                    .userId(parseLong(claims.getSubject()))
                    .role(role)
                    .expiration(expiration).build();

        } catch (JwtException e) {
            throw new UserNotFoundException();
        }
    }
}
