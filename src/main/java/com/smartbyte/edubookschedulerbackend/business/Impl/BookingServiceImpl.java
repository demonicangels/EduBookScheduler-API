package com.smartbyte.edubookschedulerbackend.business.Impl;

import com.smartbyte.edubookschedulerbackend.business.BookingService;
import com.smartbyte.edubookschedulerbackend.domain.Booking;
import com.smartbyte.edubookschedulerbackend.domain.User;
import com.smartbyte.edubookschedulerbackend.persistence.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;

    @Override
    public Booking createBooking(Booking booking) {
        return bookingRepository.createBooking(booking);
    }

    @Override
    public Optional<Booking> rescheduleBooking(Booking booking) {
        return bookingRepository.updateBooking(booking);
    }

    @Override
    public List<Booking> getUsersBooking(User us) {
        return bookingRepository.getBookingsFor(us);
    }

    @Override
    public void cancelAppointment(Booking booking) {
        bookingRepository.deleteBooking(booking);
    }
}
