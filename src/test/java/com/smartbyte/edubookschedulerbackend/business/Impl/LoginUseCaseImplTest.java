//package com.smartbyte.edubookschedulerbackend.business.Impl;
//
//import com.smartbyte.edubookschedulerbackend.business.exception.InvalidPasswordException;
//import com.smartbyte.edubookschedulerbackend.business.exception.UserNotFoundException;
//import com.smartbyte.edubookschedulerbackend.business.request.LoginRequest;
//import com.smartbyte.edubookschedulerbackend.business.response.LoginResponse;
//import com.smartbyte.edubookschedulerbackend.domain.Role;
//import com.smartbyte.edubookschedulerbackend.domain.Student;
//import com.smartbyte.edubookschedulerbackend.domain.User;
//import com.smartbyte.edubookschedulerbackend.persistence.UserRepository;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.mockito.Mockito.when;
//
//@ExtendWith(MockitoExtension.class)
//public class LoginUseCaseImplTest {
//
//    @Mock
//    private UserRepository userRepositoryMock;
//    @InjectMocks
//    private LoginUseCaseImpl loginUseCase;
//
//    /**
//     * @verifies throw UserNotFoundException if email is invalid
//     * @see LoginUseCaseImpl#Login(com.smartbyte.edubookschedulerbackend.business.request.LoginRequest)
//     */
//    @Test
//    public void Login_shouldThrowUserNotFoundExceptionIfEmailIsInvalid() {
//        //Arrange
//        LoginRequest request=LoginRequest.builder()
//                .email("user@gmail.com")
//                .password("user")
//                .build();
//
//        when(userRepositoryMock.getUserByEmail(request.getEmail())).thenReturn(Optional.empty());
//
//        //Act + Assert
//        assertThrows(UserNotFoundException.class,()->loginUseCase.Login(request));
//
//    }
//
//    /**
//     * @verifies throw InvalidPasswordException if password is invalid
//     * @see LoginUseCaseImpl#Login(com.smartbyte.edubookschedulerbackend.business.request.LoginRequest)
//     */
//    @Test
//    public void Login_shouldThrowInvalidPasswordExceptionIfPasswordIsInvalid() {
//        //Arrange
//        LoginRequest request=LoginRequest.builder()
//                .email("student@gmail.com")
//                .password("user")
//                .build();
//
//        User student= Student.builder()
//                .password("student")
//                .email("student@gmail.com")
//                .role(Role.Student)
//                .id(1L)
//                .name("student")
//                .PCN(1L)
//                .build();
//
//        when(userRepositoryMock.getUserByEmail(request.getEmail())).thenReturn(Optional.of(student));
//
//        //Act + Assert
//        assertThrows(InvalidPasswordException.class,()->loginUseCase.Login(request));
//    }
//
//    /**
//     * @verifies return login response when request is valid
//     * @see LoginUseCaseImpl#Login(com.smartbyte.edubookschedulerbackend.business.request.LoginRequest)
//     */
//    @Test
//    public void Login_shouldReturnLoginResponseWhenRequestIsValid() {
//        //Arrange
//        LoginRequest request=LoginRequest.builder()
//                .email("student@gmail.com")
//                .password("student")
//                .build();
//
//        User student= Student.builder()
//                .password("student")
//                .email("student@gmail.com")
//                .role(Role.Student)
//                .id(1L)
//                .name("student")
//                .PCN(1L)
//                .build();
//
//        LoginResponse expectedResponse=LoginResponse.builder()
//                .id(student.getId())
//                .role(student.getRole())
//                .name(student.getName())
//                .build();
//
//        when(userRepositoryMock.getUserByEmail(request.getEmail())).thenReturn(Optional.of(student));
//
//        //Act
//        LoginResponse actualResponse=loginUseCase.Login(request);
//
//        //Assert
//        assertEquals(expectedResponse,actualResponse);
//
//    }
//
//    /**
//     * @verifies throw InvalidPasswordException if password match but different case
//     * @see LoginUseCaseImpl#Login(LoginRequest)
//     */
//    @Test
//    public void Login_shouldThrowInvalidPasswordExceptionIfPasswordMatchButDifferentCase() {
//
//        //Arrange
//        LoginRequest request=LoginRequest.builder()
//                .email("student@gmail.com")
//                .password("STUDENT")
//                .build();
//
//        User student= Student.builder()
//                .password("student")
//                .email("student@gmail.com")
//                .role(Role.Student)
//                .id(1L)
//                .name("student")
//                .PCN(1L)
//                .build();
//
//        when(userRepositoryMock.getUserByEmail(request.getEmail())).thenReturn(Optional.of(student));
//
//        //Act + Assert
//        assertThrows(InvalidPasswordException.class,()->loginUseCase.Login(request));
//    }
//}
