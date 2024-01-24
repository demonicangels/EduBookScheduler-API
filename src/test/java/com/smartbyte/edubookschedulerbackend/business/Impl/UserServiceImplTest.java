package com.smartbyte.edubookschedulerbackend.business.Impl;

import com.smartbyte.edubookschedulerbackend.business.request.CreateUserRequest;
import com.smartbyte.edubookschedulerbackend.business.response.GetAssignedUserResponse;
import com.smartbyte.edubookschedulerbackend.domain.User;
import com.smartbyte.edubookschedulerbackend.persistence.jpa.entity.EntityConverter;
import com.smartbyte.edubookschedulerbackend.business.exception.UserNotFoundException;
import com.smartbyte.edubookschedulerbackend.business.response.GetUserProfileResponse;
import com.smartbyte.edubookschedulerbackend.domain.Role;
import com.smartbyte.edubookschedulerbackend.domain.Student;
import com.smartbyte.edubookschedulerbackend.domain.Tutor;
import com.smartbyte.edubookschedulerbackend.persistence.UserRepository;
import com.smartbyte.edubookschedulerbackend.persistence.jpa.entity.StudentInfoEntity;
import com.smartbyte.edubookschedulerbackend.persistence.jpa.entity.TutorInfoEntity;
import com.smartbyte.edubookschedulerbackend.persistence.jpa.entity.UserEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalLong;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
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

    /**
     * @verifies save user
     * @see UserServiceImpl#createUser(com.smartbyte.edubookschedulerbackend.business.request.CreateUserRequest)
     */
    @ParameterizedTest
    @EnumSource(Role.class)
    void createUser_shouldSaveUser(Role role){
        //Arrange
        CreateUserRequest request= CreateUserRequest.builder()
                .role(role)
                .build();

        UserEntity expectedUserEntity;

        User expectedUser;

        switch (role){

            case Student -> {
                expectedUserEntity = StudentInfoEntity.builder()
                        .name(request.getName())
                        .email(request.getEmail())
                        .password(request.getPassword())
                        .profilePicURL(request.getProfilePicURL())
                        .pcn(123L)
                        .build();

                expectedUser=Student.builder().build();
            }
            case Tutor -> {
                expectedUserEntity=TutorInfoEntity.builder()
                        .name(request.getName())
                        .email(request.getEmail())
                        .password(request.getPassword())
                        .profilePicURL(request.getProfilePicURL())
                        .build();
                expectedUser=Tutor.builder().build();
            }
            default -> {
                expectedUserEntity=null;
                expectedUser=null;
            }
        }


        when(converter.convertFromUserEntity(expectedUserEntity)).thenReturn(expectedUser);

        //Act
        User actualUser=userService.createUser(request);

        //Assert
        assertEquals(expectedUser,actualUser);

    }

    /**
     * @verifies delete User
     * @see UserServiceImpl#deleteUser(User)
     */
    @Test
    void deleteUser_shouldDeleteUser() {
        //Arrange
        User user=Tutor.builder().build();

        UserEntity userEntity=UserEntity.builder().build();

        when(converter.convertFromUser(user)).thenReturn(userEntity);

        //Act
        userService.deleteUser(user);

        //Assert
        verify(userRepositoryMock).delete(userEntity);
    }

    /**
     * @verifies return user
     * @see UserServiceImpl#getUser(long)
     */
    @Test
    void getUser_shouldReturnUser() {
        //Arrange
        UserEntity userEntity=UserEntity.builder()
                .id(1L)
                .build();

        User user=Tutor.builder()
                .id(userEntity.getId())
                .build();

        when(userRepositoryMock.findById(user.getId())).thenReturn(Optional.of(userEntity));
        when(converter.convertFromUserEntity(userEntity)).thenReturn(user);

        Optional<User>expectedResponse=Optional.of(user);

        //Act
        Optional<User>actualResponse=userService.getUser(user.getId());

        //Assert
        assertEquals(expectedResponse,actualResponse);
    }

    /**
     * @verifies return searched tutor
     * @see UserServiceImpl#searchTutorsByName(String)
     */
    @Test
    void searchTutorsByName_shouldReturnSearchedTutor() {
        //Arrange
        List<UserEntity>userEntities=List.of(
                UserEntity.builder()
                        .id(1L)
                        .name("tutor1")
                        .role(Role.Tutor.getRoleId())
                        .build(),
                UserEntity.builder()
                        .id(2L)
                        .name("tutor10")
                        .role(Role.Tutor.getRoleId())
                        .build()
        );

        when(userRepositoryMock.findByRoleAndNameContainingIgnoreCase(Role.Tutor.getRoleId(), "1"))
                .thenReturn(userEntities);


        List<GetAssignedUserResponse>expectedResponses=new ArrayList<>();

        for (UserEntity userEntity:userEntities){
            Tutor tutor=Tutor.builder()
                    .id(userEntity.getId())
                    .name(userEntity.getName())
                    .role(Role.fromRoleId(userEntity.getRole()))
                    .build();

            when(converter.convertFromUserEntity(userEntity)).thenReturn(tutor);

            expectedResponses.add(GetAssignedUserResponse.builder()
                    .id(userEntity.getId())
                    .name(userEntity.getName())
                    .profilePicUrl(userEntity.getProfilePicURL())
                    .build());
        }

        //Act
        List<GetAssignedUserResponse>actualResponses=userService.searchTutorsByName("1");

        //Assert
        assertEquals(expectedResponses,actualResponses);

    }
}
