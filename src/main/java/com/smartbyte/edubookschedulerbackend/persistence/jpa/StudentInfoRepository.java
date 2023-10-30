package com.smartbyte.edubookschedulerbackend.persistence.jpa;

import com.smartbyte.edubookschedulerbackend.persistence.jpa.entity.StudentInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentInfoRepository extends JpaRepository<StudentInfoEntity, Long> {
}
