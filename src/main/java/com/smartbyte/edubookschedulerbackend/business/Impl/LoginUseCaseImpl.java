package com.smartbyte.edubookschedulerbackend.business.Impl;

import com.smartbyte.edubookschedulerbackend.business.LoginUseCase;
import com.smartbyte.edubookschedulerbackend.business.exception.InvalidPasswordException;
import com.smartbyte.edubookschedulerbackend.business.exception.UserNotFoundException;
import com.smartbyte.edubookschedulerbackend.business.request.LoginRequest;
import com.smartbyte.edubookschedulerbackend.business.response.LoginResponse;
import com.smartbyte.edubookschedulerbackend.domain.User;
import com.smartbyte.edubookschedulerbackend.persistence.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class LoginUseCaseImpl implements LoginUseCase {
    private final UserRepository userRepository;

    @Override
    public LoginResponse Login(LoginRequest request) {
        Optional<User>optionalUser=userRepository.getUserByEmail(request.getEmail());
        if (optionalUser.isEmpty()){
            throw new UserNotFoundException();
        }

        User user=optionalUser.get();

        if (!request.getPassword().equalsIgnoreCase(user.getPassword())){
            throw new InvalidPasswordException();
        }

        return LoginResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .role(user.getRole())
                .build();
    }
}
