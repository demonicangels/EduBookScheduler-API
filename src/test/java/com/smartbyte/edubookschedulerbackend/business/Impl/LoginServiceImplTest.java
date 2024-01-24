package com.smartbyte.edubookschedulerbackend.business.Impl;

import com.smartbyte.edubookschedulerbackend.business.security.token.impl.AccessTokenDecoderEncoderImpl;
import com.smartbyte.edubookschedulerbackend.business.security.token.impl.AccessTokenImpl;
import com.smartbyte.edubookschedulerbackend.persistence.jpa.entity.EntityConverter;
import com.smartbyte.edubookschedulerbackend.business.exception.InvalidPasswordException;
import com.smartbyte.edubookschedulerbackend.business.exception.UserNotFoundException;
import com.smartbyte.edubookschedulerbackend.business.request.LoginRequest;
import com.smartbyte.edubookschedulerbackend.business.response.LoginResponse;
import com.smartbyte.edubookschedulerbackend.domain.Role;
import com.smartbyte.edubookschedulerbackend.domain.Student;
import com.smartbyte.edubookschedulerbackend.persistence.UserRepository;
import com.smartbyte.edubookschedulerbackend.persistence.jpa.entity.StudentInfoEntity;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoginServiceImplTest {

    @Mock
    private UserRepository userRepositoryMock;
    @Mock
    private EntityConverter converter;
    @Mock
    private AccessTokenDecoderEncoderImpl accessTokenDecoderEncoder;
    @InjectMocks
    private LoginServiceImpl loginUseCase;

    /**
     * @verifies throw UserNotFoundException if email is invalid
     * @see LoginServiceImpl#Login(com.smartbyte.edubookschedulerbackend.business.request.LoginRequest)
     */
    @Test
    void Login_shouldThrowUserNotFoundExceptionIfEmailIsInvalid() {
        //Arrange
        LoginRequest request=LoginRequest.builder()
                .email("user@gmail.com")
                .password("user")
                .build();

        when(userRepositoryMock.getUserByEmail(request.getEmail())).thenReturn(Optional.empty());

        //Act + Assert
        assertThrows(UserNotFoundException.class,()->loginUseCase.Login(request));

    }

    /**
     * @verifies throw InvalidPasswordException if password is invalid
     * @see LoginServiceImpl#Login(com.smartbyte.edubookschedulerbackend.business.request.LoginRequest)
     */
    @Test
    void Login_shouldThrowInvalidPasswordExceptionIfPasswordIsInvalid() {
        //Arrange
        LoginRequest request=LoginRequest.builder()
                .email("student@gmail.com")
                .password("user")
                .build();

        StudentInfoEntity studentEntity= StudentInfoEntity.builder()
                .password("student")
                .email("student@gmail.com")
                .role(0)
                .id(1L)
                .name("student")
                .pcn(1L)
                .build();

        Student student=Student.builder()
                .id(1L)
                .name(studentEntity.getName())
                .PCN(studentEntity.getPcn())
                .role(Role.Student)
                .password(studentEntity.getPassword())
                .email(studentEntity.getEmail())
                .build();

        when(userRepositoryMock.getUserByEmail(request.getEmail())).thenReturn(Optional.of(studentEntity));
        when(converter.convertFromUserEntity(studentEntity)).thenReturn(student);

        //Act + Assert
        assertThrows(InvalidPasswordException.class,()->loginUseCase.Login(request));
    }

    /**
     * @verifies return login response when request is valid
     * @see LoginServiceImpl#Login(com.smartbyte.edubookschedulerbackend.business.request.LoginRequest)
     */
    @Test
    void Login_shouldReturnLoginResponseWhenRequestIsValid() {
        //Arrange
        LoginRequest request=LoginRequest.builder()
                .email("student@gmail.com")
                .password("student")
                .build();

        StudentInfoEntity studentEntity= StudentInfoEntity.builder()
                .password("student")
                .email("student@gmail.com")
                .role(0)
                .id(1L)
                .name("student")
                .pcn(1L)
                .build();

        Student student=Student.builder()
                .id(1L)
                .name(studentEntity.getName())
                .PCN(studentEntity.getPcn())
                .role(Role.Student)
                .password(studentEntity.getPassword())
                .email(studentEntity.getEmail())
                .build();

        AccessTokenImpl accessToken=AccessTokenImpl.builder()
                .userId(student.getId())
                .role(student.getRole()).build();

        String jwts=Jwts.builder().compact();

        when(accessTokenDecoderEncoder.generateJWT(accessToken)).thenReturn(jwts);

        LoginResponse expectedResponse=LoginResponse.builder()
                .id(student.getId())
                .role(student.getRole())
                .name(student.getName())
                .accessToken(jwts)
                .build();

        when(userRepositoryMock.getUserByEmail(request.getEmail())).thenReturn(Optional.of(studentEntity));
        when(converter.convertFromUserEntity(studentEntity)).thenReturn(student);


        //Act
        LoginResponse actualResponse=loginUseCase.Login(request);

        //Assert
        assertEquals(expectedResponse,actualResponse);

    }

    /**
     * @verifies throw InvalidPasswordException if password match but different case
     * @see LoginServiceImpl#Login(LoginRequest)
     */
    @Test
    void Login_shouldThrowInvalidPasswordExceptionIfPasswordMatchButDifferentCase() {

        //Arrange
        LoginRequest request=LoginRequest.builder()
                .email("student@gmail.com")
                .password("STUDENT")
                .build();

        StudentInfoEntity studentEntity= StudentInfoEntity.builder()
                .password("student")
                .email("student@gmail.com")
                .role(0)
                .id(1L)
                .name("student")
                .pcn(1L)
                .build();

        Student student=Student.builder()
                .id(1L)
                .name(studentEntity.getName())
                .PCN(studentEntity.getPcn())
                .role(Role.Student)
                .password(studentEntity.getPassword())
                .email(studentEntity.getEmail())
                .build();

        when(userRepositoryMock.getUserByEmail(request.getEmail())).thenReturn(Optional.of(studentEntity));
        when(converter.convertFromUserEntity(studentEntity)).thenReturn(student);
        //Act + Assert
        assertThrows(InvalidPasswordException.class,()->loginUseCase.Login(request));
    }

    /**
     * @verifies throw IllegalArgumentException when user is not found
     * @see LoginServiceImpl#generateAccessToken(com.smartbyte.edubookschedulerbackend.domain.User)
     */
    @Test
    public void generateAccessToken_shouldThrowIllegalArgumentExceptionWhenUserIsNotFound() {
        //Arrange
        assertThrows(IllegalArgumentException.class,()->loginUseCase.generateAccessToken(null));
    }
}
