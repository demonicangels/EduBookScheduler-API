package com.smartbyte.edubookschedulerbackend.controller;

import com.smartbyte.edubookschedulerbackend.business.TutorAssignmentService;
import com.smartbyte.edubookschedulerbackend.business.request.AssignStudentToTutorRequest;
import com.smartbyte.edubookschedulerbackend.business.request.SearchAssignedUserByNameRequest;
import com.smartbyte.edubookschedulerbackend.business.response.GetAssignedUserResponse;
import jakarta.annotation.security.RolesAllowed;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/assignment")
@AllArgsConstructor
@CrossOrigin(origins = {"http://localhost:5173","http://localhost:4173", "http://localhost:5174"})
public class TutorAssignmentController {

    private final TutorAssignmentService tutorAssignmentService;
    @PostMapping
    ResponseEntity<Void>AssignStudentToTutor(@RequestBody AssignStudentToTutorRequest request){
        tutorAssignmentService.AssignStudentToTutor(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @RolesAllowed({"Tutor", "Student"})
    @GetMapping("/{id}")
    ResponseEntity<List<GetAssignedUserResponse>>getAssignedTutors(@PathVariable(value = "id")long id){
        return ResponseEntity.ok(tutorAssignmentService.GetStudentAssignedTutor(id));
    }

    @RolesAllowed({"Tutor", "Student"})
    @GetMapping("/search")
    ResponseEntity<List<GetAssignedUserResponse>>searchAssignedTutorsByName(@RequestBody SearchAssignedUserByNameRequest request){
        return ResponseEntity.ok(tutorAssignmentService.searchAssignedTutorByName(request));
    }

}
