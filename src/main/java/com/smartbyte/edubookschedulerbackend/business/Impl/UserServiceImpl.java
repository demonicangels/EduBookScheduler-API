package com.smartbyte.edubookschedulerbackend.business.Impl;

import com.smartbyte.edubookschedulerbackend.business.UserService;
import com.smartbyte.edubookschedulerbackend.domain.User;
import com.smartbyte.edubookschedulerbackend.persistence.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    @Override
    public User createUser(User u) {
        return userRepository.createUser(u);
    }

    @Override
    public Optional<User> getUser(long id) {
        return userRepository.getUserById(id);
    }

    @Override
    public Optional<User> updateUser(User user) {
        return userRepository.updateUser(user);
    }

    @Override
    public void deleteUser(User user) {
        userRepository.deleteUser(user);
    }
}
