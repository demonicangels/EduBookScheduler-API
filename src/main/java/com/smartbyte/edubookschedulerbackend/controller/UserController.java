package com.smartbyte.edubookschedulerbackend.controller;

import com.smartbyte.edubookschedulerbackend.business.LoginService;
import com.smartbyte.edubookschedulerbackend.business.UserService;
import com.smartbyte.edubookschedulerbackend.business.request.CreateUserRequest;
import com.smartbyte.edubookschedulerbackend.business.request.LoginRequest;
import com.smartbyte.edubookschedulerbackend.business.response.GetUserProfileResponse;
import com.smartbyte.edubookschedulerbackend.business.response.LoginResponse;
import com.smartbyte.edubookschedulerbackend.business.security.token.AccessToken;
import com.smartbyte.edubookschedulerbackend.domain.Role;
import com.smartbyte.edubookschedulerbackend.domain.User;
import jakarta.annotation.security.RolesAllowed;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
@CrossOrigin(origins = {"http://localhost:5173","http://localhost:4173", "http://localhost:5174"})
public class UserController {
    private final LoginService loginService;
    private final UserService userService;
    private final AccessToken accessToken;

    @PostMapping(value = "login")
    ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request){
        return ResponseEntity.ok(loginService.Login(request));
    }

    @PostMapping(value = "register")
    ResponseEntity<User> register(@RequestBody CreateUserRequest request){
        return ResponseEntity.ok(userService.createUser(request));
    }

    @RolesAllowed("{Tutor, Student, Admin}")
    @GetMapping("{id}")
    ResponseEntity<GetUserProfileResponse>getUserProfile(@PathVariable(value = "id") long id){
        boolean isUserAuthenticated = accessToken.getId().equals(id);
        boolean isStudent = accessToken.hasRole(Role.Student.name());
        boolean isAdmin = accessToken.hasRole(Role.Admin.name());
        boolean isTutor = accessToken.hasRole(Role.Tutor.name());

        if(isUserAuthenticated && (isAdmin || isStudent || isTutor)){
            return ResponseEntity.ok(userService.getUserProfile(id));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
