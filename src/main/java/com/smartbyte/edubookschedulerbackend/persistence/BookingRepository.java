package com.smartbyte.edubookschedulerbackend.persistence;

import com.smartbyte.edubookschedulerbackend.domain.Booking;
import com.smartbyte.edubookschedulerbackend.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUser(User user);
//    Booking createBooking(Booking booking);
//    Optional<Booking> getBookingById(long id);
//    List<Booking> getBookingsFor(User user);
//    Optional<Booking> updateBooking(Booking booking);
//    void deleteBooking(Booking booking);
}
