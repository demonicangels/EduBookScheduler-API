package com.smartbyte.edubookschedulerbackend.controller;

import com.smartbyte.edubookschedulerbackend.business.AvailabilityService;
import com.smartbyte.edubookschedulerbackend.business.request.CreateSetAvailabilityRequest;
import com.smartbyte.edubookschedulerbackend.business.request.GetAvailabilityRequest;
import com.smartbyte.edubookschedulerbackend.business.response.*;

import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/booking")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:5173","http://localhost:4173", "http://localhost:5174"})
public class AvailabilityController {
    private final AvailabilityService availabilityService;

    @RolesAllowed("{Tutor}")
    @PostMapping("/createAvailability")
    ResponseEntity<List<CreateSetAvailabilityResponse>> createAvailabilityOfTutor(@RequestBody List<CreateSetAvailabilityRequest> requests){
        List<CreateSetAvailabilityResponse>response = availabilityService.createAvailability(requests);
        return ResponseEntity.ok(response);
    }

    @RolesAllowed("{Student}")
    @PostMapping("/getTutor")
    ResponseEntity<List<GetAvailabilityResponse>> getAvailabilityOfTeachers(@RequestBody GetAvailabilityRequest request){
        List<GetAvailabilityResponse>response = availabilityService.findAvailableTeachersByDateAndTime(request);
        return ResponseEntity.ok(response);
    }

    @RolesAllowed("{Student}")
    @GetMapping("/tutorName/{id}")
    ResponseEntity<GetTutorsNameResponse> getTutorsName(@PathVariable("id") long id){
        GetTutorsNameResponse response = availabilityService.GetTutorsName(id);
        return ResponseEntity.ok(response);
    }

    @RolesAllowed("{Student}")
    @GetMapping("/getTutor")
    ResponseEntity<GetUsersResponse> getTutor(){
        GetUsersResponse response = availabilityService.GetTutors();
        return ResponseEntity.ok(response);
    }

    @RolesAllowed("{Student}")
    @GetMapping("/tutorBookings/{id}")
    ResponseEntity<GetAvailabilityTutorResponse> getTutorAvailability(@PathVariable("id") long id){
        GetAvailabilityTutorResponse response = availabilityService.getTutorsBooking(id);
        return ResponseEntity.ok(response);
    }

    @RolesAllowed("{Tutor, Student}")
    @GetMapping("/tutorAvailabilityWeekly/{id}")
    ResponseEntity<List<GetSetAvailabilityResponse>> getAvailabilityOfTutorWeekly(@PathVariable("id") long id){
        List<GetSetAvailabilityResponse> response = availabilityService.getAvailabilityOfTutorWeekly(id);
        return ResponseEntity.ok(response);
    }

}
