package com.smartbyte.edubookschedulerbackend.persistence;

import com.smartbyte.edubookschedulerbackend.persistence.jpa.entity.BookingEntity;
import com.smartbyte.edubookschedulerbackend.persistence.jpa.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
@Repository
public interface BookingRepository extends JpaRepository<BookingEntity, Long> {
    List<BookingEntity> findByStudent(UserEntity student);

    List<BookingEntity> findByTutor(UserEntity tutor);

    @Query("SELECT b FROM BookingEntity b WHERE b.student=:student AND b.date>=:currentDate ORDER BY b.date,b.startTime")
    List<BookingEntity> getUpcomingBookings(@Param("currentDate")Date currentDate,
                                      @Param("student") UserEntity student);

    List<BookingEntity> findByDateAndStartTimeAndEndTime(Date date, Integer start, Integer end);

    @Modifying
    @Query("UPDATE BookingEntity SET state=:booking_state WHERE id=:booking_id")
    void updateBookingState(@Param("booking_id") long bookingId, @Param("booking_state") Integer bookingState);

}
