package com.smartbyte.edubookschedulerbackend.controller;

import com.smartbyte.edubookschedulerbackend.business.LoginUseCase;
import com.smartbyte.edubookschedulerbackend.business.request.LoginRequest;
import com.smartbyte.edubookschedulerbackend.business.response.LoginResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController

@RequestMapping("/users")
@AllArgsConstructor
public class UserController {
    private final LoginUseCase loginUseCase;

    //TODO JAN do the controller for the booking
    @PostMapping(value = "login")
    ResponseEntity<LoginResponse> Login(@RequestBody LoginRequest request){
        return ResponseEntity.ok(loginUseCase.Login(request));
    }
}
