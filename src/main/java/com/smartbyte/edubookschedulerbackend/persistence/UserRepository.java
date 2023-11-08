package com.smartbyte.edubookschedulerbackend.persistence;

import com.smartbyte.edubookschedulerbackend.domain.User;
import com.smartbyte.edubookschedulerbackend.persistence.jpa.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    UserEntity getUserById(long id);

    List<UserEntity> findByRole(Integer role);
    List<UserEntity> findByIdNotInAndRoleNot(List<Long> ids, Integer role);

    Optional<UserEntity> findByEmail(String email);
}
