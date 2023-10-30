package com.smartbyte.edubookschedulerbackend.business.Impl;

import com.smartbyte.edubookschedulerbackend.business.exception.UserNotFoundException;
import com.smartbyte.edubookschedulerbackend.business.response.GetUserProfileResponse;
import com.smartbyte.edubookschedulerbackend.domain.Role;
import com.smartbyte.edubookschedulerbackend.domain.Student;
import com.smartbyte.edubookschedulerbackend.domain.Tutor;
import com.smartbyte.edubookschedulerbackend.persistence.UserRepository;
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
public class UserServiceImplTest {
    @Mock
    private UserRepository userRepositoryMock;
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
    public void getUserProfile_shouldReturnATutorProfileIfUserIdBelongsToATutor() {

        //Arrange
        Tutor tutor=Tutor.builder()
                .id(1L)
                .name("tutor")
                .email("tutor@gmail.com")
                .role(Role.Tutor)
                .password("tutor")
                .build();

        GetUserProfileResponse expectedResponse=GetUserProfileResponse.builder()
                .id(tutor.getId())
                .PCN(OptionalLong.empty())
                .role(tutor.getRole())
                .email(tutor.getEmail())
                .name(tutor.getName())
                .build();

        when(userRepositoryMock.getUserById(1)).thenReturn(Optional.of(tutor));

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
    public void getUserProfile_shouldReturnAStudentProfileIfUserIdBelongsToAStudent() {

        //Arrange
        Student student=Student.builder()
                .id(1L)
                .name("student")
                .email("student@gmail.com")
                .role(Role.Student)
                .password("student")
                .PCN(1L)
                .build();

        GetUserProfileResponse expectedResponse=GetUserProfileResponse.builder()
                .id(student.getId())
                .PCN(OptionalLong.of(student.getPCN()))
                .role(student.getRole())
                .email(student.getEmail())
                .name(student.getName())
                .build();

        when(userRepositoryMock.getUserById(1)).thenReturn(Optional.of(student));

        //Act
        GetUserProfileResponse actualResponse=userService.getUserProfile(1L);

        //Assert
        assertEquals(expectedResponse,actualResponse);
    }
}
