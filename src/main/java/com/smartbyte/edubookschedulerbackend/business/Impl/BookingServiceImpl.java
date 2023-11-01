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

    public Booking createBooking(Booking booking) {
        return bookingRepository.save(booking);
    }

    @Override
    public Optional<Booking> rescheduleBooking(Booking booking) {
        return Optional.of(bookingRepository.save(booking));
    }

    @Override
    public Optional<Booking> getBookingById(long id) {
        return bookingRepository.findById(id);
    }

    @Override
    public List<Booking> getUsersBooking(User user) {
        return bookingRepository.findByUser(user);
    }

    @Override
    public void cancelAppointment(Booking booking) {
        bookingRepository.delete(booking);
    }



}
