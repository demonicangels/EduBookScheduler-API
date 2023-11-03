package com.smartbyte.edubookschedulerbackend.controller;

import com.smartbyte.edubookschedulerbackend.business.AvailabilityService;
import com.smartbyte.edubookschedulerbackend.business.request.CreateBookingRequest;
import com.smartbyte.edubookschedulerbackend.business.request.GetAvailabilityRequest;
import com.smartbyte.edubookschedulerbackend.business.response.GetAvailabilityResponse;
import com.smartbyte.edubookschedulerbackend.business.response.GetUsersBookingResponse;
import com.smartbyte.edubookschedulerbackend.domain.Booking;
import com.smartbyte.edubookschedulerbackend.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/booking")
@RequiredArgsConstructor
public class AvailabilityController {
    private final AvailabilityService availabilityService;

    @GetMapping("/filter")
    ResponseEntity<List<GetAvailabilityResponse>> getAvailabilityOfTeachers(@RequestBody GetAvailabilityRequest request){
        List<GetAvailabilityResponse>response = availabilityService.findAvailableTeachersByDateAndTime(request);
        return ResponseEntity.ok(response);
    }
}
