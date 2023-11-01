package com.smartbyte.edubookschedulerbackend.persistence;

import com.smartbyte.edubookschedulerbackend.persistence.jpa.UserRepository;
import com.smartbyte.edubookschedulerbackend.persistence.jpa.entity.UserEntity;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public class UserRepositoryTest {
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private UserRepository userRepo;

    @Test
    void save_shouldSaveTheCorrectFields(){
        // Arrange
        UserEntity savedUser = UserEntity.builder()
                .name("Test")
                .email("test@test.com")
                .password("123")
                .role(1)
                .build();

        // Act
        savedUser = userRepo.save(savedUser);

        // Assert
        assertNotNull(savedUser.getId());
        savedUser = entityManager.find(UserEntity.class, savedUser.getId());
        
        UserEntity expectedUser = UserEntity.builder()
                .id(savedUser.getId())
                .name("Test")
                .email("test@test.com")
                .password("123")
                .role(1)
                .build();
        assertEquals(expectedUser, savedUser);
    }
}
