package com.smartbyte.edubookschedulerbackend.business.Impl;

import com.smartbyte.edubookschedulerbackend.business.AuthService;
import com.smartbyte.edubookschedulerbackend.business.exception.UserNotFoundException;
import com.smartbyte.edubookschedulerbackend.business.request.LoginRequest;
import com.smartbyte.edubookschedulerbackend.business.response.JWTResponse;
import com.smartbyte.edubookschedulerbackend.business.security.token.impl.AccessTokenDecoderEncoderImpl;
import com.smartbyte.edubookschedulerbackend.business.security.token.impl.AccessTokenImpl;
import com.smartbyte.edubookschedulerbackend.domain.Role;
import com.smartbyte.edubookschedulerbackend.domain.Student;
import com.smartbyte.edubookschedulerbackend.domain.Tutor;
import com.smartbyte.edubookschedulerbackend.domain.User;
import com.smartbyte.edubookschedulerbackend.persistence.UserRepository;
import com.smartbyte.edubookschedulerbackend.persistence.jpa.entity.EntityConverter;
import com.smartbyte.edubookschedulerbackend.persistence.jpa.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

   private final EntityConverter converter;
   private final PasswordEncoder passwordEncoder;
   private final UserRepository userRepository;
   private final AccessTokenDecoderEncoderImpl accessTokenService;


    /**
     * @return user when logged in
     * @should return a user when credentials are correct
     * @should return exception if credentials are not correct
     * @should return doctor obj if a doctor is logged in
     * @should return client obj if a client is logged in
     */
    @Override
    public JWTResponse loginUser(LoginRequest loginRequest){
        String accessToken ="";

        Optional<User>  loggedInUser = Optional.ofNullable(userRepository.findByEmail(loginRequest.getEmail()).map(converter :: convertFromUserEntity).orElse(null));

        if(loggedInUser.isEmpty()){
            throw new UserNotFoundException();
        }
        if(!passMatch(loginRequest.getPassword(), loggedInUser.get().getPassword())){
            throw new UserNotFoundException();
        }
        if (loggedInUser.get() instanceof Tutor tutor) {

            tutor = (Tutor) loggedInUser.get();

            accessToken = generateAccessToken(tutor);


        } else if (loggedInUser.get() instanceof Student student) {

            student = (Student) loggedInUser.get();

            accessToken = generateAccessToken(student);
        }

        return JWTResponse.builder()
                .accessToken(accessToken)
                .build();
    }

    /**
     * @param usrId
     * @should return true if a user with the same id is found in the database
     * @should return false if no user with that id is found in the db
     * @return true if user found in db
     * @return false if no user is found
     */
    @Override
    public Boolean authenticateUser(Long usrId) {
        Optional<UserEntity> userEntity = userRepository.getUserById(usrId);

        return userEntity.isPresent();
    }

    public boolean passMatch(String rawPass, String encodedPass){
        return passwordEncoder.matches(rawPass, encodedPass);
    }

    /**
     * @param user
     * @return accessToken
     * @should return an accessToken based on the loggedIn user
     * @should return IllegalArgument exception when user is null
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
