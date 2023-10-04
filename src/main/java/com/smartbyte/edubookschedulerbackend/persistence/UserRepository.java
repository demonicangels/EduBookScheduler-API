package com.smartbyte.edubookschedulerbackend.persistence;

import com.smartbyte.edubookschedulerbackend.domain.User;

import java.util.Optional;

public interface UserRepository {
    Optional<User> getUserByEmail(String email);
}
