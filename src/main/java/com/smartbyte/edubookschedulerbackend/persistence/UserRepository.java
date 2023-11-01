package com.smartbyte.edubookschedulerbackend.persistence;

import com.smartbyte.edubookschedulerbackend.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> getUserByEmail(String email);

    Optional<User> getUserById(long id);
//    User createUser(User user);
//    Optional<User> getUserById(long id);
//    Optional<User> getUserByEmail(String email);
//    List<User> getUsers();
//    Optional<User> updateUser(User user);
//    void deleteUser(User user);
}
