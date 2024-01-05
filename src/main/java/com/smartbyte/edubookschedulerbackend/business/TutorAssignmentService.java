package com.smartbyte.edubookschedulerbackend.business;

import com.smartbyte.edubookschedulerbackend.domain.Student;
import com.smartbyte.edubookschedulerbackend.domain.Tutor;

import java.util.List;
import java.util.Set;

public interface TutorAssignmentService {
    void AssignStudentToTutor(long tutorId, long studentId);
    List<Student> GetTutorAssignedStudents(long tutorId);
    List<Tutor> GetStudentAssignedTutor(long studentId);
}
