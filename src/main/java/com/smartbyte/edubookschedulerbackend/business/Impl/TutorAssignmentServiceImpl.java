package com.smartbyte.edubookschedulerbackend.business.Impl;

import com.smartbyte.edubookschedulerbackend.business.TutorAssignmentService;
import com.smartbyte.edubookschedulerbackend.domain.Student;
import com.smartbyte.edubookschedulerbackend.domain.Tutor;
import com.smartbyte.edubookschedulerbackend.persistence.UserRepository;
import com.smartbyte.edubookschedulerbackend.persistence.jpa.entity.EntityConverter;
import com.smartbyte.edubookschedulerbackend.persistence.jpa.entity.StudentInfoEntity;
import com.smartbyte.edubookschedulerbackend.persistence.jpa.entity.TutorInfoEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class TutorAssignmentServiceImpl implements TutorAssignmentService {

    final UserRepository userRepo;
    final EntityConverter converter;

    @Override
    public void AssignStudentToTutor(long tutorId, long studentId) {
        TutorInfoEntity tutorInfo = (TutorInfoEntity) userRepo.getUserById(tutorId).get();
        StudentInfoEntity studentInfo = (StudentInfoEntity) userRepo.getUserById(studentId).get();

        tutorInfo.getStudents().add(studentInfo);
        userRepo.save(tutorInfo);
    }

    @Override
    public List<Student> GetTutorAssignedStudents(long tutorId) {
        return ((TutorInfoEntity)userRepo.getUserById(tutorId).get()).getStudents().stream().map(s -> (Student)converter.convertFromUserEntity(s)).toList();
    }

    @Override
    public List<Tutor> GetStudentAssignedTutor(long studentId) {
        return ((StudentInfoEntity)userRepo.getUserById(studentId).get()).getTutors().stream().map(s -> (Tutor)converter.convertFromUserEntity(s)).toList();
    }
}
