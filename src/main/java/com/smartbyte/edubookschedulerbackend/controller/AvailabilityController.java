package com.smartbyte.edubookschedulerbackend.controller;

import com.smartbyte.edubookschedulerbackend.business.AvailabilityService;
import com.smartbyte.edubookschedulerbackend.business.UserService;
import com.smartbyte.edubookschedulerbackend.business.request.CreateSetAvailabilityRequest;
import com.smartbyte.edubookschedulerbackend.business.request.GetAvailabilityRequest;
import com.smartbyte.edubookschedulerbackend.business.response.*;

import com.smartbyte.edubookschedulerbackend.business.security.token.AccessToken;
import com.smartbyte.edubookschedulerbackend.domain.Role;
import com.smartbyte.edubookschedulerbackend.domain.User;
import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/booking")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:5173","http://localhost:4173", "http://localhost:5174"})
public class AvailabilityController {
    private final UserService userService;
    private final AvailabilityService availabilityService;
    private final AccessToken accessToken;

    @RolesAllowed("{Tutor}")
    @PostMapping("/createAvailability")
    ResponseEntity<List<CreateSetAvailabilityResponse>> createAvailabilityOfTutor(@RequestBody List<CreateSetAvailabilityRequest> requests){

        boolean isTutor = accessToken.hasRole(Role.Tutor.name());
        boolean isAuthorizedUser = requests.stream()
                .anyMatch(request -> request.getTutorId().equals(accessToken.getId()));

        if(isTutor && isAuthorizedUser){
            List<CreateSetAvailabilityResponse>response = availabilityService.createAvailability(requests);
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();


    }


    @RolesAllowed("{Student}")
    @PostMapping("/getTutor")
    ResponseEntity<List<GetAvailabilityResponse>> getAvailabilityOfTeachers(@RequestBody GetAvailabilityRequest request){
        boolean isStudent = accessToken.hasRole(Role.Student.name());


        if(isStudent){
            List<GetAvailabilityResponse>response = availabilityService.findAvailableTeachersByDateAndTime(request);
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @RolesAllowed("{Student}")
    @GetMapping("/tutorName/{id}")
    ResponseEntity<GetTutorsNameResponse> getTutorsName(@PathVariable("id") long id){
        boolean isStudent = accessToken.hasRole(Role.Student.name());

        if(isStudent){
            GetTutorsNameResponse response = availabilityService.GetTutorsName(id);
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

    }

    @RolesAllowed("{Student}")
    @GetMapping("/getTutor")
    ResponseEntity<GetUsersResponse> getTutor(){
        boolean isStudent = accessToken.hasRole(Role.Student.name());

        if(isStudent){
            GetUsersResponse response = availabilityService.GetTutors();
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @RolesAllowed("{Student}")
    @GetMapping("/tutorBookings/{id}")
    ResponseEntity<GetAvailabilityTutorResponse> getTutorAvailability(@PathVariable("id") long id){
        boolean isStudent = accessToken.hasRole(Role.Student.name());

        if(isStudent){
            GetAvailabilityTutorResponse response = availabilityService.getTutorsBooking(id);
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @RolesAllowed("{Tutor, Student}")
    @GetMapping("/tutorAvailabilityWeekly/{id}")
    ResponseEntity<List<GetSetAvailabilityResponse>> getAvailabilityOfTutorWeekly(@PathVariable("id") long id){
        boolean isStudent = accessToken.hasRole(Role.Student.name());
        boolean isTutor = accessToken.hasRole(Role.Tutor.name());


        if(isStudent || isTutor){
            List<GetSetAvailabilityResponse> response = availabilityService.getAvailabilityOfTutorWeekly(id);
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

}
