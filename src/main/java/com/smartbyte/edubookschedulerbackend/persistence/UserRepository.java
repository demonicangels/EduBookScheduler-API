package com.smartbyte.edubookschedulerbackend.persistence;


import com.smartbyte.edubookschedulerbackend.persistence.jpa.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> getUserByEmail(String email);

    Optional<UserEntity> getUserById(long id);

    UserEntity findByNameAndRole(String name, Integer role);

    List<UserEntity> findByRole(Integer role);
    List<UserEntity> findByIdNotInAndRoleNot(List<Long> ids, Integer role);

    Optional<UserEntity> findByEmail(String email);

    @Query("SELECT tie.students FROM TutorInfoEntity tie WHERE tie.id=:id")
    Set<UserEntity> getTutorAssignedStudents(@Param("id") long tutorId);

    @Query("SELECT sie.tutors FROM StudentInfoEntity sie WHERE sie.id=:id")
    Set<UserEntity> getStudentAssignedTutors(@Param("id") long studentId);

    List<UserEntity>findByRoleAndNameContainingIgnoreCase(int role,String name);

}
