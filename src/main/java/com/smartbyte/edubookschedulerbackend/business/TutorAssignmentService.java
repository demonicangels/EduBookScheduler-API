package com.smartbyte.edubookschedulerbackend.business;

import com.smartbyte.edubookschedulerbackend.business.request.AssignStudentToTutorRequest;
import com.smartbyte.edubookschedulerbackend.business.response.GetAssignedUserResponse;

import java.util.List;

public interface TutorAssignmentService {
    void AssignStudentToTutor(AssignStudentToTutorRequest request);
    List<GetAssignedUserResponse> GetTutorAssignedStudents(long tutorId);
    List<GetAssignedUserResponse> GetStudentAssignedTutor(long studentId);
}
