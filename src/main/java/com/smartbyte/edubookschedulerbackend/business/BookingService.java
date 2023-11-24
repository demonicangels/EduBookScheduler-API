package com.smartbyte.edubookschedulerbackend.business;

import com.smartbyte.edubookschedulerbackend.business.request.UpdateBookingStateRequest;
import com.smartbyte.edubookschedulerbackend.business.response.GetUpcomingBookingsResponse;
import com.smartbyte.edubookschedulerbackend.domain.Booking;
import com.smartbyte.edubookschedulerbackend.domain.User;

import java.util.*;

public interface BookingService {
    /*
        FIXME: should return optional in case there is any error (ex: tutor is already booked for that time or data
               is inconsistent.
    */
    Optional<Booking> createBooking(Booking booking);
    Optional<Booking> rescheduleBooking(Booking booking);

    List<GetUpcomingBookingsResponse> getUpcomingBookings(long studentId);

    Optional<Booking> getBookingById(long id);
    List<Booking> getUsersBooking(User us);
    void cancelAppointment(Booking booking);

    Booking createBooking2(Booking booking, String Date);

    void updateBookingState(UpdateBookingStateRequest request);

}
