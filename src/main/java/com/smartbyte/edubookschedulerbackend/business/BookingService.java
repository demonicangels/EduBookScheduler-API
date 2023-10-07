package com.smartbyte.edubookschedulerbackend.business;

import com.smartbyte.edubookschedulerbackend.domain.Booking;
import com.smartbyte.edubookschedulerbackend.domain.User;

import java.util.*;

public interface BookingService {
    Booking createBooking(Booking booking);
    Optional<Booking> rescheduleBooking(Booking booking);
    List<Booking> getUsersBooking(User us);
    void cancelAppointment(Booking booking);
}
