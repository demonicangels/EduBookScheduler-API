package com.smartbyte.edubookschedulerbackend.persistence;

import com.smartbyte.edubookschedulerbackend.persistence.jpa.StudentInfoRepository;
import com.smartbyte.edubookschedulerbackend.persistence.jpa.entity.StudentInfoEntity;
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
public class StudentInfoRepositoryTest {
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private StudentInfoRepository studentInfoRepo;

    @Test
    void save_shouldSaveTheCorrectFields(){
        // Arrange
        UserEntity testUser = UserEntity.builder()
                .name("Test")
                .email("test@test.com")
                .password("123")
                .role(0)
                .build();
        entityManager.persist(testUser);

        StudentInfoEntity savedStudentInfo = StudentInfoEntity.builder()
                .user(testUser)
                .pcn(1337L)
                .build();

        // Act
        savedStudentInfo = studentInfoRepo.save(savedStudentInfo);

        // Assert
        assertNotNull(savedStudentInfo.getId());
        savedStudentInfo = entityManager.find(StudentInfoEntity.class, savedStudentInfo.getId());

        StudentInfoEntity expectedStudentInfo = StudentInfoEntity.builder()
                .id(savedStudentInfo.getId())
                .user(testUser)
                .pcn(1337L)
                .build();
        assertEquals(expectedStudentInfo, savedStudentInfo);
    }
}
