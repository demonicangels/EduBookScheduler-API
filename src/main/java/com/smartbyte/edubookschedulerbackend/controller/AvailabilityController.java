package com.smartbyte.edubookschedulerbackend.controller;

import com.smartbyte.edubookschedulerbackend.business.AvailabilityService;
import com.smartbyte.edubookschedulerbackend.business.request.GetAvailabilityRequest;
import com.smartbyte.edubookschedulerbackend.business.response.GetAvailabilityResponse;

import com.smartbyte.edubookschedulerbackend.business.response.GetAvailabilityTutorResponse;
import com.smartbyte.edubookschedulerbackend.business.response.GetUsersResponse;
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

    @PostMapping("/getTutor")
    ResponseEntity<List<GetAvailabilityResponse>> getAvailabilityOfTeachers(@RequestBody GetAvailabilityRequest request){
        List<GetAvailabilityResponse>response = availabilityService.findAvailableTeachersByDateAndTime(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/getTutor")
    ResponseEntity<GetUsersResponse> getTutor(){
        GetUsersResponse response = availabilityService.GetTutors();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/GetTutorAvailability")
    ResponseEntity<GetAvailabilityTutorResponse> getTutorAvailability(@RequestBody int id){
        GetAvailabilityTutorResponse response = availabilityService.getTutorsBooking(id);
        return ResponseEntity.ok(response);
    }
}
