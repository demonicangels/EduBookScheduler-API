package com.smartbyte.edubookschedulerbackend.business.Impl;

import com.smartbyte.edubookschedulerbackend.business.request.CreateUserRequest;
import com.smartbyte.edubookschedulerbackend.business.response.GetAssignedUserResponse;
import com.smartbyte.edubookschedulerbackend.domain.Role;
import com.smartbyte.edubookschedulerbackend.domain.Tutor;
import com.smartbyte.edubookschedulerbackend.persistence.jpa.entity.EntityConverter;
import com.smartbyte.edubookschedulerbackend.business.UserService;
import com.smartbyte.edubookschedulerbackend.business.exception.UserNotFoundException;
import com.smartbyte.edubookschedulerbackend.business.response.GetUserProfileResponse;
import com.smartbyte.edubookschedulerbackend.domain.Student;
import com.smartbyte.edubookschedulerbackend.domain.User;
import com.smartbyte.edubookschedulerbackend.persistence.UserRepository;
import com.smartbyte.edubookschedulerbackend.persistence.jpa.entity.StudentInfoEntity;
import com.smartbyte.edubookschedulerbackend.persistence.jpa.entity.TutorInfoEntity;
import com.smartbyte.edubookschedulerbackend.persistence.jpa.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.OptionalLong;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final EntityConverter converter;

    /**
     *
     * @param request CreateUserRequest
     * @return User
     *
     * @should save user
     */
    @Override
    public User createUser(CreateUserRequest request) {

        UserEntity user = saveUser(request);
        userRepository.save(user);

        return converter.convertFromUserEntity(user);
    }

    /**
     *
     * @param id user id
     * @return Optional of User
     *
     * @should return user
     */
    @Override
    public Optional<User> getUser(long id) {
        return userRepository.findById(id)
                .map(converter::convertFromUserEntity);
    }
    @Override
    public Optional<User> updateUser(User user) {
        if (userRepository.existsById(user.getId())) {
            return Optional.of(converter.convertFromUserEntity(userRepository.save(converter.convertFromUser(user))));
        } else {
            return Optional.empty();
        }
    }

    /**
     *
     * @param user User
     *
     * @should delete User
     */

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

    /**
     *
     * @param name tutor name
     * @return list of tutor
     *
     * @should return searched tutor
     */
    @Override
    public List<GetAssignedUserResponse> searchTutorsByName(String name) {
        List<UserEntity>userEntities=userRepository.findByRoleAndNameContainingIgnoreCase(
                Role.Tutor.getRoleId(),
                name
                );

        return userEntities.stream().map(userEntity -> {
            Tutor tutor=(Tutor) converter.convertFromUserEntity(userEntity);
            return GetAssignedUserResponse.builder()
                    .id(tutor.getId())
                    .name(tutor.getName())
                    .profilePicUrl(tutor.getProfilePicURL())
                    .build();
        }).toList();


    }

    private UserEntity saveUser(CreateUserRequest request) {

        return switch (request.getRole()){
            case Student -> StudentInfoEntity.builder()
                    .name(request.getName())
                    .email(request.getEmail())
                    .password(request.getPassword())
                    .profilePicURL(request.getProfilePicURL())
                    .pcn(123L)
                    .build();
            case Tutor -> TutorInfoEntity.builder()
                    .name(request.getName())
                    .email(request.getEmail())
                    .password(request.getPassword())
                    .profilePicURL(request.getProfilePicURL())
                    .build();
            default -> null;
        };
  }
}
