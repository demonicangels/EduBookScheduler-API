package com.smartbyte.edubookschedulerbackend.business.Impl;

import com.smartbyte.edubookschedulerbackend.business.BookingService;
import com.smartbyte.edubookschedulerbackend.business.EntityConverter;
import com.smartbyte.edubookschedulerbackend.domain.Booking;
import com.smartbyte.edubookschedulerbackend.domain.Role;
import com.smartbyte.edubookschedulerbackend.domain.User;
import com.smartbyte.edubookschedulerbackend.persistence.BookingRepository;
import com.smartbyte.edubookschedulerbackend.persistence.jpa.entity.BookingEntity;
import com.smartbyte.edubookschedulerbackend.persistence.jpa.entity.UserEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.RollbackException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {


    private final BookingRepository bookingRepository;
    private final EntityConverter converter;
    @PersistenceContext
    private EntityManager entityManager;
    public Optional<Booking> createBooking(Booking booking) {
        return Optional.of(converter.convertFromBookingEntity(bookingRepository.save(converter.convertFromBooking(booking))));
    }

    @Override
    public Optional<Booking> rescheduleBooking(Booking booking) {
        return Optional.of(converter.convertFromBookingEntity(bookingRepository.save(converter.convertFromBooking(booking))));
    }

    @Override
    public Optional<Booking> getBookingById(long id) {
        return Optional.of(converter.convertFromBookingEntity(bookingRepository.findById(id).get()));
    }

    @Override
    public List<Booking> getUsersBooking(User user) {
        List<Booking> bookings = new ArrayList<>();

        switch (user.getRole().ordinal()) {
            case 0 -> {
                for (BookingEntity b : bookingRepository.findByStudent(converter.convertFromUser(user))) {
                    bookings.add(converter.convertFromBookingEntity(b));
                }
            }
            case 1 -> {
                for (BookingEntity b : bookingRepository.findByTutor(converter.convertFromUser(user))) {
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

    @Override
    @Transactional(rollbackOn = Exception.class)
    public Booking createBooking2(Booking booking, String Date) {
        try {
            Date date = convertStringToDate(Date);
            booking.setDate(date);
            UserEntity student = converter.convertFromUser(booking.getStudent());
            UserEntity tutor = converter.convertFromUser(booking.getTutor());
            BookingEntity data = BookingEntity.builder()
                    .date(booking.getDate())
                    .description(booking.getDescription())
                    .startTime(booking.getStartTime())
                    .endTime(booking.getEndTime())
                    .student(student)
                    .tutor(tutor)
                    .build();
            BookingEntity dataEntity = bookingRepository.save(entityManager.merge(data));
            Booking dataDomain = converter.convertFromBookingEntity(dataEntity);
            return dataDomain;
        } catch (Exception e) {
            // Log the exception or print its stack trace to identify the issue
            e.printStackTrace();
            throw new RuntimeException("Error creating booking", e);
        }
    }


    private Date convertStringToDate(String dateString) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
            return inputFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }


}
