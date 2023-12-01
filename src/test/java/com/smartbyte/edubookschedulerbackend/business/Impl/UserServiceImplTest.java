package com.smartbyte.edubookschedulerbackend.business.Impl;

import com.smartbyte.edubookschedulerbackend.persistence.jpa.entity.EntityConverter;
import com.smartbyte.edubookschedulerbackend.business.exception.UserNotFoundException;
import com.smartbyte.edubookschedulerbackend.business.response.GetUserProfileResponse;
import com.smartbyte.edubookschedulerbackend.domain.Role;
import com.smartbyte.edubookschedulerbackend.domain.Student;
import com.smartbyte.edubookschedulerbackend.domain.Tutor;
import com.smartbyte.edubookschedulerbackend.persistence.UserRepository;
import com.smartbyte.edubookschedulerbackend.persistence.jpa.entity.StudentInfoEntity;
import com.smartbyte.edubookschedulerbackend.persistence.jpa.entity.UserEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.OptionalLong;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    private UserRepository userRepositoryMock;
    @Mock
    private EntityConverter converter;

    @InjectMocks
    private UserServiceImpl userService;

    /**
     * @verifies throw UserNotFoundException if user id is invalid
     * @see UserServiceImpl#getUserProfile(long)
     */
    @Test
    void getUserProfile_shouldThrowUserNotFoundExceptionIfUserIdIsInvalid() {
       //Arrange

       when(userRepositoryMock.getUserById(1)).thenReturn(Optional.empty());

       //Act + Assert
        assertThrows(UserNotFoundException.class,()->userService.getUserProfile(1));
    }

    /**
     * @verifies return a tutor profile if user id belongs to a tutor
     * @see UserServiceImpl#getUserProfile(long)
     */
    @Test
    void getUserProfile_shouldReturnATutorProfileIfUserIdBelongsToATutor() {

        //Arrange
        UserEntity tutorEntity=UserEntity.builder()
                .id(1L)
                .name("tutor")
                .email("tutor@gmail.com")
                .role(1)
                .password("tutor")
                .build();

        Tutor tutor=Tutor.builder()
                .id(tutorEntity.getId())
                .name(tutorEntity.getName())
                .password(tutorEntity.getPassword())
                .role(Role.Tutor)
                .email(tutorEntity.getEmail())
                .build();

        GetUserProfileResponse expectedResponse=GetUserProfileResponse.builder()
                .id(tutor.getId())
                .PCN(OptionalLong.empty())
                .role(Role.Tutor)
                .email(tutor.getEmail())
                .name(tutor.getName())
                .build();

        when(userRepositoryMock.getUserById(1)).thenReturn(Optional.of(tutorEntity));

        when(converter.convertFromUserEntity(tutorEntity)).thenReturn(tutor);

        //Act
        GetUserProfileResponse actualResponse=userService.getUserProfile(1L);

        //Assert
        assertEquals(expectedResponse,actualResponse);

    }

    /**
     * @verifies return a student profile if user id belongs to a student
     * @see UserServiceImpl#getUserProfile(long)
     */
    @Test
    void getUserProfile_shouldReturnAStudentProfileIfUserIdBelongsToAStudent() {

        //Arrange
        StudentInfoEntity studentEntity=StudentInfoEntity.builder()
                .id(1L)
                .name("student")
                .email("student@gmail.com")
                .role(0)
                .password("student")
                .pcn(1L)
                .build();

        Student student=Student.builder()
                .id(studentEntity.getId())
                .name(studentEntity.getName())
                .password(studentEntity.getPassword())
                .role(Role.Student)
                .email(studentEntity.getEmail())
                .PCN(studentEntity.getPcn())
                .build();

        GetUserProfileResponse expectedResponse=GetUserProfileResponse.builder()
                .id(student.getId())
                .PCN(OptionalLong.of(studentEntity.getPcn()))
                .role(Role.Student)
                .email(student.getEmail())
                .name(student.getName())
                .build();

        when(userRepositoryMock.getUserById(1)).thenReturn(Optional.of(studentEntity));
        when(converter.convertFromUserEntity(studentEntity)).thenReturn(student);

        //Act
        GetUserProfileResponse actualResponse=userService.getUserProfile(1L);

        //Assert
        assertEquals(expectedResponse,actualResponse);
    }
}
