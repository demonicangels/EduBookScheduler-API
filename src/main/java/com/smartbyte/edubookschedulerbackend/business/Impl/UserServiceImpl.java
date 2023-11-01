package com.smartbyte.edubookschedulerbackend.business.Impl;

import com.smartbyte.edubookschedulerbackend.business.UserService;
import com.smartbyte.edubookschedulerbackend.business.exception.UserNotFoundException;
import com.smartbyte.edubookschedulerbackend.business.response.GetUserProfileResponse;
import com.smartbyte.edubookschedulerbackend.domain.Student;
import com.smartbyte.edubookschedulerbackend.domain.User;
import com.smartbyte.edubookschedulerbackend.persistence.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.OptionalLong;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User createUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public Optional<User> getUser(long id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> updateUser(User user) {
        if (userRepository.existsById(user.getId())) {
            return Optional.of(userRepository.save(user));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public void deleteUser(User user) {
        userRepository.delete(user);
    }

    /**
     *
     * @param id user id
     * @return user profile
     *
     * @should throw UserNotFoundException if user id is invalid
     * @should return a tutor profile if user id belongs to a tutor
     * @should return a student profile if user id belongs to a student
     */
    @Override
    public GetUserProfileResponse getUserProfile(long id) {

        //Get User by id from repository
        Optional<User>optionalUser=userRepository.getUserById(id);

        //Throw exception if user is not found
        if(optionalUser.isEmpty()){
            throw new UserNotFoundException();
        }

        //get the user data
        User user=optionalUser.get();

        // return PCN value if user is a student, otherwise return a null
        OptionalLong PCN=(user instanceof Student)
                ? OptionalLong.of(((Student) user).getPCN())
                : OptionalLong.empty();

        //return the response back to the controller
        return GetUserProfileResponse.builder()
                .id(id)
                .email(user.getEmail())
                .profilePicURL(user.getProfilePicURL())
                .name(user.getName())
                .role(user.getRole())
                .PCN(PCN)
                .build();

    }
}
