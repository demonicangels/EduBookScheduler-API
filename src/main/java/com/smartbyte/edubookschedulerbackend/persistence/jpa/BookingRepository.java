package com.smartbyte.edubookschedulerbackend.persistence.jpa;

import com.smartbyte.edubookschedulerbackend.persistence.jpa.entity.BookingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<BookingEntity, Long> {
}
