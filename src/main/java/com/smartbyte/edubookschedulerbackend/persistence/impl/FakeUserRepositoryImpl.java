package com.smartbyte.edubookschedulerbackend.persistence.impl;

import com.smartbyte.edubookschedulerbackend.domain.Role;
import com.smartbyte.edubookschedulerbackend.domain.Student;
import com.smartbyte.edubookschedulerbackend.domain.Tutor;
import com.smartbyte.edubookschedulerbackend.domain.User;
import com.smartbyte.edubookschedulerbackend.persistence.UserRepository;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.IntStream;

@Repository
public class FakeUserRepositoryImpl implements UserRepository {

    private long _ID_COUNTER = 3;
    private final List<User> users;

    public FakeUserRepositoryImpl(){
        users = new ArrayList<>(List.of(
                Student.builder()
                        .id((long)1)
                        .name("Nicolita la niña bonita")
                        .email("nikol@gmail.com")
                        .password("12345678")
                        .PCN(1234567)
                        .build(),
                Tutor.builder()
                        .id((long)2)
                        .name("Capi")
                        .email("capi@gmail.com")
                        .password("12345678")
                        .build()
        ));
    }

    @Override
    public User createUser(User user) {
        user.setId(_ID_COUNTER++);
        users.add(user);
        return user;
    }

    @Override
    public Optional<User> getUserById(long id) {
        return users.stream().filter(u -> ((Long)id).equals(u.getId())).findFirst();
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        return users.stream().filter((user)->user.getEmail().equalsIgnoreCase(email)).findFirst();
    }

    @Override
    public List<User> getUsers() {
        return Collections.unmodifiableList(users);
    }

    @Override
    public Optional<User> updateUser(User user) {
        Long nullUserId = user.getId();
        if(nullUserId == null) return Optional.empty();
        long userId = (long)nullUserId;
        Optional<User> optOldUser = this.getUserById(userId);
        if(optOldUser.isEmpty()) return Optional.empty();
        User oldUser = optOldUser.get();
        oldUser.setEmail(user.getEmail());
        oldUser.setPassword(user.getPassword());
        return Optional.of(oldUser);
    }

    @Override
    public void deleteUser(User user) {
        OptionalInt optIdx = IntStream.range(0, users.size())
                .filter(i -> users.get(i).getId().equals(user.getId()))
                .findFirst();
        if(optIdx.isPresent())
            users.remove(optIdx.getAsInt());
    }
}
