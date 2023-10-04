package com.smartbyte.edubookschedulerbackend.persistence.impl;

import com.smartbyte.edubookschedulerbackend.domain.User;
import com.smartbyte.edubookschedulerbackend.persistence.UserRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class FakeUserRepositoryImpl implements UserRepository {
    private final List<User>users;

    public FakeUserRepositoryImpl() {
        this.users=new ArrayList<>();
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        return users.stream().filter((user)->user.getEmail().equalsIgnoreCase(email)).findFirst();
    }
}
