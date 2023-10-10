package com.smartbyte.edubookschedulerbackend.controller;

import com.smartbyte.edubookschedulerbackend.business.LoginUseCase;
import com.smartbyte.edubookschedulerbackend.business.UserService;
import com.smartbyte.edubookschedulerbackend.business.request.LoginRequest;
import com.smartbyte.edubookschedulerbackend.business.response.GetUserProfileResponse;
import com.smartbyte.edubookschedulerbackend.business.response.LoginResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController

@RequestMapping("/users")
@AllArgsConstructor
@CrossOrigin(origins = {"http://localhost:5173","http://localhost:4173"})
public class UserController {
    private final LoginUseCase loginUseCase;
    private final UserService userService;
    @PostMapping(value = "login")
    ResponseEntity<LoginResponse> Login(@RequestBody LoginRequest request){
        return ResponseEntity.ok(loginUseCase.Login(request));
    }

    @GetMapping("{id}")
    ResponseEntity<GetUserProfileResponse>getUserProfile(@PathVariable(value = "id") long id){
        return ResponseEntity.ok(userService.getUserProfile(id));
    }
}
