package com.smartbyte.edubookschedulerbackend.business.Impl;

import com.smartbyte.edubookschedulerbackend.business.TutorAssignmentService;
import com.smartbyte.edubookschedulerbackend.business.exception.UserNotFoundException;
import com.smartbyte.edubookschedulerbackend.business.request.AssignStudentToTutorRequest;
import com.smartbyte.edubookschedulerbackend.business.response.GetAssignedUserResponse;
import com.smartbyte.edubookschedulerbackend.domain.Student;
import com.smartbyte.edubookschedulerbackend.domain.Tutor;
import com.smartbyte.edubookschedulerbackend.persistence.UserRepository;
import com.smartbyte.edubookschedulerbackend.persistence.jpa.entity.EntityConverter;
import com.smartbyte.edubookschedulerbackend.persistence.jpa.entity.StudentInfoEntity;
import com.smartbyte.edubookschedulerbackend.persistence.jpa.entity.TutorInfoEntity;
import com.smartbyte.edubookschedulerbackend.persistence.jpa.entity.UserEntity;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TutorAssignmentServiceImpl implements TutorAssignmentService {

    private final UserRepository userRepo;
    private final EntityConverter converter;

    /**
     *
      * @param request Assign student to tutor request
     *
     * @should modify the tutor assignments
     */
    @Override
    @Transactional
    public void AssignStudentToTutor(AssignStudentToTutorRequest request) {
        Optional<UserEntity> optionalTutor=userRepo.getUserById(request.getTutorId());

        Tutor tutor=convertToTutor(optionalTutor);

        tutor.setAssignedStudents(request.getStudentIds()
                .stream().map(studentId->
                        convertToStudent(userRepo.getUserById(studentId)))
                .toList());

        TutorInfoEntity tutorInfo=converter.convertFromTutor(tutor);

        userRepo.save(tutorInfo);
    }

    private Tutor convertToTutor(Optional<UserEntity> optionalTutor){
        TutorInfoEntity tutorInfo=(TutorInfoEntity) isUserValid(optionalTutor);
        if (tutorInfo.getRole()==1){
            return converter.convertFromTutorEntity(tutorInfo);
        }
        throw new IllegalArgumentException("Role is invalid!");

    }

    private Student convertToStudent(Optional<UserEntity> optionalStudent){
        StudentInfoEntity studentInfo=(StudentInfoEntity) isUserValid(optionalStudent);
        if (studentInfo.getRole()==0){
            return converter.convertFromStudentEntity(studentInfo);
        }
        throw new IllegalArgumentException("Role is invalid!");
    }

    private UserEntity isUserValid(Optional<UserEntity>optionalUser){
        if (optionalUser.isEmpty()){
            throw new UserNotFoundException();
        }
        return optionalUser.get();

    }

    /**
     *
     * @param tutorId tutor id
     * @return List of assigned students
     *
     * @should throw UserNotFoundException if tutor is not found
     * @should throw IllegalArgumentException if role is not a tutor
     * @should return list of assigned students
     */
    @Override
    public List<GetAssignedUserResponse> GetTutorAssignedStudents(long tutorId) {
        Tutor tutor=convertToTutor(userRepo.getUserById(tutorId));
        List<GetAssignedUserResponse>responses=new ArrayList<>();
        for (Student student:tutor.getAssignedStudents()){
            responses.add(GetAssignedUserResponse.builder()
                    .id(student.getId())
                    .name(student.getName())
                    .profilePicUrl(student.getProfilePicURL())
                    .build());
        }
        return responses;
    }

    /**
     *
     * @param studentId student id
     * @return List of assigned tutors
     *
     * @should throw IllegalArgumentException if role is not a student
     * @should return list of assigned tutors
     */
    @Override
    public List<GetAssignedUserResponse> GetStudentAssignedTutor(long studentId) {
        Student student=convertToStudent(userRepo.getUserById(studentId));
        List<GetAssignedUserResponse>responses=new ArrayList<>();
        for (Tutor tutor:student.getAssignedTutors()){
            responses.add(GetAssignedUserResponse.builder()
                    .id(tutor.getId())
                    .name(tutor.getName())
                    .profilePicUrl(tutor.getProfilePicURL())
                    .build());
        }
        return responses;
    }
}
