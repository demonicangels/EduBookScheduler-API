package com.smartbyte.edubookschedulerbackend.persistence;

import com.smartbyte.edubookschedulerbackend.persistence.jpa.entity.AvailabilityEntity;
import com.smartbyte.edubookschedulerbackend.persistence.jpa.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface AvailabilityRepository extends JpaRepository<AvailabilityEntity, Long> {
    List<AvailabilityEntity> findAllByTutorAndDateBetween(UserEntity tutor, Date startDate, Date endDate);
    List<AvailabilityEntity> findAllByTutorAndDate(UserEntity tutor,Date date);
}
