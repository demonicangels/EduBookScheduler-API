package com.smartbyte.edubookschedulerbackend.business.Impl;

import com.smartbyte.edubookschedulerbackend.business.BookingService;
import com.smartbyte.edubookschedulerbackend.business.EntityConverter;
import com.smartbyte.edubookschedulerbackend.domain.Booking;
import com.smartbyte.edubookschedulerbackend.domain.Role;
import com.smartbyte.edubookschedulerbackend.domain.User;
import com.smartbyte.edubookschedulerbackend.persistence.BookingRepository;
import com.smartbyte.edubookschedulerbackend.persistence.jpa.entity.BookingEntity;
import com.smartbyte.edubookschedulerbackend.persistence.jpa.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {


    private final BookingRepository bookingRepository;
    private final EntityConverter converter;

    public Booking createBooking(Booking booking) {
        return converter.convertFromBookingEntity(bookingRepository.save(converter.convertFromBooking(booking)));
    }

    @Override
    public Optional<Booking> rescheduleBooking(Booking booking) {
        return Optional.of(converter.convertFromBookingEntity(bookingRepository.save(converter.convertFromBooking(booking))));
    }

    @Override
    public Optional<Booking> getBookingById(long id) { return Optional.of(converter.convertFromBookingEntity(bookingRepository.findById(id).get()));}

    @Override
    public List<Booking> getUsersBooking(User user) {
        List<Booking> bookings = new ArrayList<>();

        switch(user.getRole().ordinal()){
            case 0 -> {
                for(BookingEntity b : bookingRepository.findByStudent(converter.convertFromUser(user))){
                    bookings.add(converter.convertFromBookingEntity(b));
                }
            }
            case 1 -> {
                for(BookingEntity b : bookingRepository.findByTutor(converter.convertFromUser(user))){
                    bookings.add(converter.convertFromBookingEntity(b));
                }
            }
        }

        return bookings;
    }

    @Override
    public void cancelAppointment(Booking booking) {
        bookingRepository.delete(converter.convertFromBooking(booking));
    }



}
