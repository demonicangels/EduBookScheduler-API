package com.smartbyte.edubookschedulerbackend.persistence;

import com.smartbyte.edubookschedulerbackend.domain.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    User createUser(User user);
    Optional<User> getUserById(long id);
    Optional<User> getUserByEmail(String email);
    List<User> getUsers();
    Optional<User> updateUser(User user);
    void deleteUser(User user);
}
