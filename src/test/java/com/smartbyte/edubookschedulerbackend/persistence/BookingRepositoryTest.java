package com.smartbyte.edubookschedulerbackend.persistence;

import com.smartbyte.edubookschedulerbackend.persistence.jpa.BookingRepository;
import com.smartbyte.edubookschedulerbackend.persistence.jpa.entity.BookingEntity;
import com.smartbyte.edubookschedulerbackend.persistence.jpa.entity.UserEntity;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public class BookingRepositoryTest {
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private BookingRepository bookingRepo;

    @Test
    void save_shouldSaveTheCorrectFields(){
        // Arrange
        UserEntity testTutor = UserEntity.builder()
                .name("TestTeacher")
                .email("tutor@test.com")
                .password("123")
                .role(1)
                .build();

        UserEntity testStudent = UserEntity.builder()
                .name("TestStudent")
                .email("student@test.com")
                .password("123")
                .role(0)
                .build();

        entityManager.persist(testStudent);
        entityManager.persist(testTutor);
        entityManager.flush();
        entityManager.clear();

        BookingEntity savedBooking = BookingEntity.builder()
                .date(new Date(2002, 7, 12))
                .startTime(900)
                .endTime(960)
                .description("Test booking")
                .student(testStudent)
                .tutor(testTutor)
                .build();

        // Act
        savedBooking = bookingRepo.save(savedBooking);

        // Assert
        assertNotNull(savedBooking.getId());
        savedBooking = entityManager.find(BookingEntity.class, savedBooking.getId());

        BookingEntity expectedBooking = BookingEntity.builder()
                .id(savedBooking.getId())
                .date(new Date(2002, 7, 12))
                .startTime(900)
                .endTime(960)
                .description("Test booking")
                .student(testStudent)
                .tutor(testTutor)
                .build();
        assertEquals(expectedBooking, savedBooking);
    }
}
