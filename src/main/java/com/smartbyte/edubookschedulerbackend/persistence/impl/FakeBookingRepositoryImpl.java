package com.smartbyte.edubookschedulerbackend.persistence.impl;

import com.smartbyte.edubookschedulerbackend.domain.Booking;
import com.smartbyte.edubookschedulerbackend.domain.Role;
import com.smartbyte.edubookschedulerbackend.domain.User;
import com.smartbyte.edubookschedulerbackend.persistence.BookingRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.stream.IntStream;

@Repository
public class FakeBookingRepositoryImpl implements BookingRepository {
    List<Booking> bookings = new ArrayList<>();
    private long _ID_COUNTER = 1;

    @Override
    public Booking createBooking(Booking booking) {
        booking.setId(_ID_COUNTER++);
        bookings.add(booking);
        return booking;
    }

    @Override
    public Optional<Booking> getBookingById(long id) {
        return bookings.stream().filter(b -> ((Long)id).equals(b.getId())).findFirst();
    }

    @Override
    public List<Booking> getBookingsFor(User user) {
        return switch (user.getRole()) {
            case Student -> bookings.stream().filter(b -> b.getStudent().getId().equals(user.getId())).toList();
            case Tutor -> bookings.stream().filter(b -> b.getTutor().getId().equals(user.getId())).toList();
            default -> List.of();
        };
    }

    @Override
    public Optional<Booking> updateBooking(Booking booking) {
        Long nullBookingId = booking.getId();
        if(nullBookingId == null) return Optional.empty();
        long bookingId = (long)nullBookingId;
        Optional<Booking> optOldBooking = this.getBookingById(bookingId);
        if(optOldBooking.isEmpty()) return Optional.empty();
        Booking oldBooking = optOldBooking.get();
        oldBooking.setDateAndTime(booking.getDateAndTime());
        oldBooking.setDescription(booking.getDescription());
        return Optional.of(oldBooking);
    }

    @Override
    public void deleteBooking(Booking booking) {
        OptionalInt optIdx = IntStream.range(0, bookings.size())
                .filter(i -> bookings.get(i).getId().equals(booking.getId()))
                .findFirst();
        if(optIdx.isPresent())
            bookings.remove(optIdx.getAsInt());
    }
}
