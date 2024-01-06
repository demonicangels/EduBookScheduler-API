package com.smartbyte.edubookschedulerbackend.business.Impl;

import com.smartbyte.edubookschedulerbackend.business.exception.UserNotFoundException;
import com.smartbyte.edubookschedulerbackend.business.request.AssignStudentToTutorRequest;
import com.smartbyte.edubookschedulerbackend.business.response.GetAssignedUserResponse;
import com.smartbyte.edubookschedulerbackend.domain.Role;
import com.smartbyte.edubookschedulerbackend.domain.Student;
import com.smartbyte.edubookschedulerbackend.domain.Tutor;
import com.smartbyte.edubookschedulerbackend.persistence.UserRepository;
import com.smartbyte.edubookschedulerbackend.persistence.jpa.entity.EntityConverter;
import com.smartbyte.edubookschedulerbackend.persistence.jpa.entity.StudentInfoEntity;
import com.smartbyte.edubookschedulerbackend.persistence.jpa.entity.TutorInfoEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TutorAssignmentServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private EntityConverter entityConverter;
    @InjectMocks
    TutorAssignmentServiceImpl tutorAssignmentService;

    /**
     * @verifies modify the tutor assignments
     * @see TutorAssignmentServiceImpl#AssignStudentToTutor(com.smartbyte.edubookschedulerbackend.business.request.AssignStudentToTutorRequest)
     */
    @Test
    void AssignStudentToTutor_shouldModifyTheTutorAssignments() {
        //Arrange
        AssignStudentToTutorRequest request=AssignStudentToTutorRequest.builder()
                .tutorId(1L)
                .studentIds(List.of(2L,3L))
                .build();

        List<StudentInfoEntity> studentInfos=new ArrayList<>();

        List<Student>students=new ArrayList<>();


        for(long studentId:request.getStudentIds()){
            StudentInfoEntity studentInfo=StudentInfoEntity.builder()
                    .id(studentId)
                    .role(0)
                    .build();

            studentInfos.add(studentInfo);

            Student student=Student.builder()
                    .id(studentInfo.getId())
                    .role(Role.Student)
                    .build();

            students.add(student);

            when(userRepository.getUserById(studentId)).thenReturn(Optional.of(studentInfo));
            when(entityConverter.convertFromStudentEntity(studentInfo)).thenReturn(student);
        }

        TutorInfoEntity tutorInfo=TutorInfoEntity.builder()
                .id(request.getTutorId())
                .role(1)
                .students(List.of())
                .build();

        when(userRepository.getUserById(request.getTutorId())).thenReturn(Optional.of(tutorInfo));


        Tutor tutor=Tutor.builder()
                .id(tutorInfo.getId())
                .assignedStudents(List.of())
                .role(Role.Tutor)
                .build();

        when(entityConverter.convertFromTutorEntity(tutorInfo)).thenReturn(tutor);

        tutor.setAssignedStudents(students);
        tutorInfo.setStudents(studentInfos);

        when(entityConverter.convertFromTutor(tutor)).thenReturn(tutorInfo);

        //Act
        tutorAssignmentService.AssignStudentToTutor(request);

        //Assert
        verify(userRepository).save(tutorInfo);

    }

    /**
     * @verifies throw UserNotFoundException if tutor is not found
     * @see TutorAssignmentServiceImpl#GetTutorAssignedStudents(long)
     */
    @Test
    void GetTutorAssignedStudents_shouldThrowUserNotFoundExceptionIfTutorIsNotFound() {
        //Arrange
        when(userRepository.getUserById(1L)).thenReturn(Optional.empty());

        //Act + Assert
        assertThrows(UserNotFoundException.class,()->tutorAssignmentService.GetTutorAssignedStudents(1L));

    }

    /**
     * @verifies throw IllegalArgumentException if role is not a tutor
     * @see TutorAssignmentServiceImpl#GetTutorAssignedStudents(long)
     */
    @Test
    void GetTutorAssignedStudents_shouldThrowIllegalArgumentExceptionIfRoleIsNotATutor() {
        //Arrange
        TutorInfoEntity tutorInfo=TutorInfoEntity.builder()
                .id(1L)
                .role(0)
                .build();

        when(userRepository.getUserById(tutorInfo.getId())).thenReturn(Optional.of(tutorInfo));

        //Act + Assert
        assertThrows(IllegalArgumentException.class,()->tutorAssignmentService.GetTutorAssignedStudents(1L));
    }

    /**
     * @verifies return list of assigned students
     * @see TutorAssignmentServiceImpl#GetTutorAssignedStudents(long)
     */
    @Test
    void GetTutorAssignedStudents_shouldReturnListOfAssignedStudents() {
        //Arrange
        List<StudentInfoEntity>studentInfos=List.of(
                StudentInfoEntity.builder()
                        .id(2L)
                        .role(0)
                        .name("student1")
                        .profilePicURL("pic2.png")
                        .build(),
                StudentInfoEntity.builder()
                        .id(3L)
                        .role(0)
                        .name("student2")
                        .profilePicURL("pic3.png")
                        .build()
        );
        List<Student>students=new ArrayList<>();
        List<GetAssignedUserResponse>expectedResponses=new ArrayList<>();


        for (StudentInfoEntity studentInfo:studentInfos){
            Student student=Student.builder()
                    .id(studentInfo.getId())
                    .profilePicURL(studentInfo.getProfilePicURL())
                    .name(studentInfo.getName())
                    .role(Role.Student)
                    .build();

            students.add(student);

            expectedResponses.add(GetAssignedUserResponse.builder()
                    .id(student.getId())
                    .name(student.getName())
                    .profilePicUrl(student.getProfilePicURL())
                    .build());
        }

        TutorInfoEntity tutorInfo=TutorInfoEntity.builder()
                .id(1L)
                .role(1)
                .name("tutor")
                .students(studentInfos)
                .profilePicURL("pic1.png")
                .build();

        when(userRepository.getUserById(tutorInfo.getId())).thenReturn(Optional.of(tutorInfo));

        Tutor tutor=Tutor.builder()
                .id(tutorInfo.getId())
                .name(tutorInfo.getName())
                .role(Role.Tutor)
                .profilePicURL(tutorInfo.getProfilePicURL())
                .assignedStudents(students)
                .build();

        when(entityConverter.convertFromTutorEntity(tutorInfo)).thenReturn(tutor);

        //Act
        List<GetAssignedUserResponse>actualResponses=tutorAssignmentService.GetTutorAssignedStudents(tutorInfo.getId());

        //Assert
        assertEquals(expectedResponses,actualResponses);

    }

    /**
     * @verifies throw IllegalArgumentException if role is not a student
     * @see TutorAssignmentServiceImpl#GetStudentAssignedTutor(long)
     */
    @Test
    void GetStudentAssignedTutor_shouldThrowIllegalArgumentExceptionIfRoleIsNotAStudent() {
        //Arrange
        StudentInfoEntity studentInfo=StudentInfoEntity.builder()
                .id(1L)
                .role(1)
                .build();

        when(userRepository.getUserById(studentInfo.getId())).thenReturn(Optional.of(studentInfo));

        //Act + Assert
        assertThrows(IllegalArgumentException.class,()->tutorAssignmentService.GetStudentAssignedTutor(1L));
    }

    /**
     * @verifies return list of assigned tutors
     * @see TutorAssignmentServiceImpl#GetStudentAssignedTutor(long)
     */
    @Test
    void GetStudentAssignedTutor_shouldReturnListOfAssignedTutors() {
        //Arrange
        List<TutorInfoEntity>tutorInfos=List.of(
                TutorInfoEntity.builder()
                        .id(2L)
                        .role(1)
                        .name("tutor1")
                        .profilePicURL("pic2.png")
                        .build(),
                TutorInfoEntity.builder()
                        .id(3L)
                        .role(1)
                        .name("tutor2")
                        .profilePicURL("pic3.png")
                        .build()
        );
        List<Tutor>tutors=new ArrayList<>();
        List<GetAssignedUserResponse>expectedResponses=new ArrayList<>();


        for (TutorInfoEntity tutorInfo:tutorInfos){
            Tutor tutor=Tutor.builder()
                    .id(tutorInfo.getId())
                    .profilePicURL(tutorInfo.getProfilePicURL())
                    .name(tutorInfo.getName())
                    .role(Role.Tutor)
                    .build();

            tutors.add(tutor);

            expectedResponses.add(GetAssignedUserResponse.builder()
                    .id(tutor.getId())
                    .name(tutor.getName())
                    .profilePicUrl(tutor.getProfilePicURL())
                    .build());
        }

        StudentInfoEntity studentInfo=StudentInfoEntity.builder()
                .id(1L)
                .role(0)
                .name("student")
                .tutors(tutorInfos)
                .profilePicURL("pic1.png")
                .build();

        when(userRepository.getUserById(studentInfo.getId())).thenReturn(Optional.of(studentInfo));

        Student student=Student.builder()
                .id(studentInfo.getId())
                .name(studentInfo.getName())
                .role(Role.Student)
                .profilePicURL(studentInfo.getProfilePicURL())
                .assignedTutors(tutors)
                .build();

        when(entityConverter.convertFromStudentEntity(studentInfo)).thenReturn(student);

        //Act
        List<GetAssignedUserResponse>actualResponses=tutorAssignmentService.GetStudentAssignedTutor(student.getId());

        //Assert
        assertEquals(expectedResponses,actualResponses);
    }
}
