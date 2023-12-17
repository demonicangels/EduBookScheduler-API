package com.smartbyte.edubookschedulerbackend.persistence;

import com.smartbyte.edubookschedulerbackend.domain.BookingRequestAnswer;
import com.smartbyte.edubookschedulerbackend.persistence.jpa.entity.BookingEntity;
import com.smartbyte.edubookschedulerbackend.persistence.jpa.entity.BookingRequestEntity;
import com.smartbyte.edubookschedulerbackend.persistence.jpa.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BookingRequestRepository extends JpaRepository<BookingRequestEntity, Long> {

    @Modifying
    @Query("UPDATE BookingRequestEntity sbr SET sbr.answer=:answer WHERE sbr.id=:reqId")
    void updateAnswer(@Param("reqId") Long reqId, @Param("answer") BookingRequestAnswer answer);

    List<BookingRequestEntity> findBookingRequestsEntitiesByReceiver(UserEntity receiver);
    List<BookingRequestEntity> findBookingRequestsEntitiesByRequester(UserEntity requester);
    @Query("SELECT bre FROM BookingRequestEntity bre WHERE (bre.bookingToSchedule = :bookingToSchedule) AND " +
            "((bre.requester = :user1 AND bre.receiver = :user2) OR (bre.requester = :user2 AND bre.receiver = :user1))")
    Optional<BookingRequestEntity> findPreviousRequest(@Param("user1") UserEntity user1, @Param("user2") UserEntity user2,
                                                       @Param("bookingToSchedule") BookingEntity bookingToSchedule);
}
