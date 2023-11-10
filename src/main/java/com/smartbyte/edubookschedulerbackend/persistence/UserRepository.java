package com.smartbyte.edubookschedulerbackend.persistence;

import com.smartbyte.edubookschedulerbackend.domain.Role;
import com.smartbyte.edubookschedulerbackend.domain.User;
import com.smartbyte.edubookschedulerbackend.persistence.jpa.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> getUserByEmail(String email);

    Optional<UserEntity> getUserById(long id);

    UserEntity findByNameAndRole(String name, Integer role);

    List<UserEntity> findByRole(Integer role);
    List<UserEntity> findByIdNotInAndRoleNot(List<Long> ids, Integer role);

    Optional<UserEntity> findByEmail(String email);
}
