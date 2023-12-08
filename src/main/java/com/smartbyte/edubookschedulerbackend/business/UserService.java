package com.smartbyte.edubookschedulerbackend.business;

import com.smartbyte.edubookschedulerbackend.business.request.CreateUserRequest;
import com.smartbyte.edubookschedulerbackend.business.response.GetUserProfileResponse;
import com.smartbyte.edubookschedulerbackend.domain.Role;
import com.smartbyte.edubookschedulerbackend.domain.User;

import java.util.Optional;

public interface UserService {
    User createUser(CreateUserRequest request);
    Optional<User> getUser(long id);
    Optional<User> updateUser(User user);
    void deleteUser(User user);
    GetUserProfileResponse getUserProfile(long id);

    Optional<User> getTutorByName (String name);
}
