package com.smartbyte.edubookschedulerbackend.persistence;

import com.smartbyte.edubookschedulerbackend.domain.Booking;
import com.smartbyte.edubookschedulerbackend.domain.User;

import java.util.List;
import java.util.Optional;

public interface BookingRepository {
    Booking createBooking(Booking booking);
    Optional<Booking> getBookingById(long id);
    List<Booking> getBookingsFor(User user);
    Optional<Booking> updateBooking(Booking booking);
    void deleteBooking(Booking booking);
}
