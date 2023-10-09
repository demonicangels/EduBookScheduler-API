package com.smartbyte.edubookschedulerbackend.business;

import com.smartbyte.edubookschedulerbackend.domain.Booking;
import com.smartbyte.edubookschedulerbackend.domain.User;

import java.util.*;

public interface BookingService {
    /*
        FIXME: should return optional in case there is any error (ex: tutor is already booked for that time or data
               is inconsistent.
    */
    Booking createBooking(Booking booking);
    Optional<Booking> rescheduleBooking(Booking booking);
    Optional<Booking> getBookingById(long id);
    List<Booking> getUsersBooking(User us);
    void cancelAppointment(Booking booking);
}
