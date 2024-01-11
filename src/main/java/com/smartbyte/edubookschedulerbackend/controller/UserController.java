package com.smartbyte.edubookschedulerbackend.controller;

import com.smartbyte.edubookschedulerbackend.business.LoginService;
import com.smartbyte.edubookschedulerbackend.business.UserService;
import com.smartbyte.edubookschedulerbackend.business.request.CreateUserRequest;
import com.smartbyte.edubookschedulerbackend.business.request.LoginRequest;
import com.smartbyte.edubookschedulerbackend.business.response.GetAssignedUserResponse;
import com.smartbyte.edubookschedulerbackend.business.response.GetUserProfileResponse;
import com.smartbyte.edubookschedulerbackend.business.response.LoginResponse;
import com.smartbyte.edubookschedulerbackend.domain.User;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
@CrossOrigin(origins = {"http://localhost:5173","http://localhost:4173", "http://localhost:5174"})
public class UserController {
    private final LoginService loginService;
    private final UserService userService;
    @PostMapping(value = "login")
    ResponseEntity<LoginResponse> Login(@RequestBody LoginRequest request){
        return ResponseEntity.ok(loginService.Login(request));
    }

    @PostMapping(value = "register")
    ResponseEntity<User> Register(@RequestBody CreateUserRequest request){
        return ResponseEntity.ok(userService.createUser(request));
    }

    @GetMapping("{id}")
    ResponseEntity<GetUserProfileResponse>getUserProfile(@PathVariable(value = "id") long id){
        return ResponseEntity.ok(userService.getUserProfile(id));
    }

    @GetMapping("/tutor/{name}")
    ResponseEntity<List<GetAssignedUserResponse>>searchTutorByName(@PathVariable String name){
        List<GetAssignedUserResponse>responses=userService.searchTutorsByName(name);
        return ResponseEntity.ok(responses);
    }
}
