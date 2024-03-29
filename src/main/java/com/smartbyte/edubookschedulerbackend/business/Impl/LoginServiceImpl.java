package com.smartbyte.edubookschedulerbackend.business.Impl;

import com.smartbyte.edubookschedulerbackend.business.LoginService;
import com.smartbyte.edubookschedulerbackend.business.exception.InvalidPasswordException;
import com.smartbyte.edubookschedulerbackend.business.exception.UserNotFoundException;
import com.smartbyte.edubookschedulerbackend.business.request.LoginRequest;
import com.smartbyte.edubookschedulerbackend.business.response.LoginResponse;
import com.smartbyte.edubookschedulerbackend.business.security.token.impl.AccessTokenDecoderEncoderImpl;
import com.smartbyte.edubookschedulerbackend.business.security.token.impl.AccessTokenImpl;
import com.smartbyte.edubookschedulerbackend.domain.Role;
import com.smartbyte.edubookschedulerbackend.domain.User;
import com.smartbyte.edubookschedulerbackend.persistence.UserRepository;
import com.smartbyte.edubookschedulerbackend.persistence.jpa.entity.EntityConverter;
import com.smartbyte.edubookschedulerbackend.persistence.jpa.entity.UserEntity;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class LoginServiceImpl implements LoginService {
    private final UserRepository userRepository;
    private final EntityConverter entityConverter;
    private final AccessTokenDecoderEncoderImpl accessTokenService;

    /**
     *
     * @param request login request
     * @return login response
     *
     * @should throw UserNotFoundException if email is invalid
     * @should throw InvalidPasswordException if password is invalid
     * @should throw InvalidPasswordException if password match but different case
     * @should return login response when request is valid
     */
    @Override
    public LoginResponse Login(LoginRequest request) {

        Optional<UserEntity>optionalUser = userRepository.getUserByEmail(request.getEmail());

        if (optionalUser.isEmpty()){
            throw new UserNotFoundException();
        }

        User user = entityConverter.convertFromUserEntity(optionalUser.get());


        if (!request.getPassword().equals(user.getPassword())){
            throw new InvalidPasswordException();
        }

        String accessToken = generateAccessToken(user);

        return LoginResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .profilePicURL(user.getProfilePicURL())
                .role(user.getRole())
                .accessToken(accessToken)
                .build();
    }

    /**
     *
     * @param user User object
     * @return access token
     *
     * @should throw IllegalArgumentException when user is not found
     */

    public String generateAccessToken(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        Long userId = user.getId();
        Role role = user.getRole();

        return accessTokenService.generateJWT(
                AccessTokenImpl.builder()
                        .userId(userId)
                        .role(role).build());
    }
}
