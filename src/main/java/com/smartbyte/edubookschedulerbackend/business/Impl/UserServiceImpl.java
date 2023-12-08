package com.smartbyte.edubookschedulerbackend.business.Impl;

import com.smartbyte.edubookschedulerbackend.business.request.CreateUserRequest;
import com.smartbyte.edubookschedulerbackend.persistence.jpa.entity.EntityConverter;
import com.smartbyte.edubookschedulerbackend.business.UserService;
import com.smartbyte.edubookschedulerbackend.business.exception.UserNotFoundException;
import com.smartbyte.edubookschedulerbackend.business.response.GetUserProfileResponse;
import com.smartbyte.edubookschedulerbackend.domain.Student;
import com.smartbyte.edubookschedulerbackend.domain.User;
import com.smartbyte.edubookschedulerbackend.persistence.UserRepository;
import com.smartbyte.edubookschedulerbackend.persistence.jpa.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.OptionalLong;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final EntityConverter converter;

    @Override
    public User createUser(CreateUserRequest request) {

        UserEntity user = saveUser(request);
        userRepository.save(user);

        return converter.convertFromUserEntity(user);
    }

    @Override
    public Optional<User> getUser(long id) {
        return Optional.of(converter.convertFromUserEntity(userRepository.findById(id).get()));
    }

    @Override
    public Optional<User> updateUser(User user) {
        if (userRepository.existsById(user.getId())) {
            return Optional.of(converter.convertFromUserEntity(userRepository.save(converter.convertFromUser(user))));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public void deleteUser(User user) {
        userRepository.delete(converter.convertFromUser(user));
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
        Optional<UserEntity>optionalUser = userRepository.getUserById(id);

        //Throw exception if user is not found
        if(optionalUser.isEmpty()) {
            throw new UserNotFoundException();
        }

        //get the user data
        User user = converter.convertFromUserEntity(optionalUser.get());

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

    @Override
    public Optional<User> getTutorByName(String name) {
        UserEntity user = userRepository.findByNameAndRole(name,1);
        return Optional.of(converter.convertFromUserEntity(user));
    }

    private UserEntity saveUser(CreateUserRequest request) {

        Integer roleInt = 0;

        if (request.getRole().equals("Tutor"))
        {
            roleInt = 1;
        }
        else if (request.getRole().equals("Student"))
    {
            roleInt= 0;
        }

        return UserEntity.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(request.getPassword())
                .profilePicURL(request.getProfilePicURL())
                .role(roleInt)
                .build();
  }
}
