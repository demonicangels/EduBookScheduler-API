package com.smartbyte.edubookschedulerbackend.business.Impl;

import com.smartbyte.edubookschedulerbackend.business.BookingService;
import com.smartbyte.edubookschedulerbackend.business.EntityConverter;
import com.smartbyte.edubookschedulerbackend.business.exception.BookingNotFoundException;
import com.smartbyte.edubookschedulerbackend.business.exception.InvalidBookingStateException;
import com.smartbyte.edubookschedulerbackend.business.exception.InvalidNewBookingStateException;
import com.smartbyte.edubookschedulerbackend.business.exception.UserNotFoundException;
import com.smartbyte.edubookschedulerbackend.business.request.UpdateBookingStateRequest;
import com.smartbyte.edubookschedulerbackend.business.response.GetUpcomingBookingsResponse;
import com.smartbyte.edubookschedulerbackend.domain.Booking;
import com.smartbyte.edubookschedulerbackend.domain.State;
import com.smartbyte.edubookschedulerbackend.domain.User;
import com.smartbyte.edubookschedulerbackend.persistence.BookingRepository;
import com.smartbyte.edubookschedulerbackend.persistence.UserRepository;
import com.smartbyte.edubookschedulerbackend.persistence.jpa.entity.BookingEntity;
import com.smartbyte.edubookschedulerbackend.persistence.jpa.entity.UserEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {


    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
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

    /**
     *
     * @param studentId student's id
     * @return list of upcoming bookings responses
     *
     * @should throw UserNotFoundException when student is not found
     * @should return an empty list when there is no upcoming booking
     * @should return a list of bookings when there are upcoming bookings
     *
     */
    @Override
    public List<GetUpcomingBookingsResponse> getUpcomingBookings(long studentId) {
        //check if user exists
        Optional<UserEntity>user=userRepository.getUserById(studentId);
        if (user.isEmpty()){
            throw new UserNotFoundException();
        }

        LocalDate currentDate=LocalDate.now();
        LocalTime currentTime=LocalTime.now();

        int minutes=currentTime.getHour()*60+currentTime.getMinute();

        //Fetch the bookings
        List<BookingEntity>bookingEntities= bookingRepository.getUpcomingBookings(
                Date.from(currentDate.atStartOfDay(ZoneId.systemDefault()).toInstant()), user.get());

        List<GetUpcomingBookingsResponse>responses=new ArrayList<>();

        SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");

        //Return list of responses
        for(BookingEntity bookingEntity:bookingEntities){
            LocalDate date=bookingEntity.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            if (date.isAfter(currentDate)||(date.equals(currentDate)&&bookingEntity.getStartTime()>minutes)){
                responses.add(GetUpcomingBookingsResponse.builder()
                        .id(bookingEntity.getId())
                        .tutorName(bookingEntity.getTutor().getName())
                        .startHour(bookingEntity.getStartTime()/60)
                        .startMinute(bookingEntity.getStartTime()%60)
                        .date(dateFormat.format(bookingEntity.getDate()))
                        .description(bookingEntity.getDescription())
                        .build());
            }
        }

        return responses;
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
                    .state(0)
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

    /**
     *
     * @param request Update booking state request
     *
     * @should throw BookingNotFoundException if booking is not found
     * @should throw InvalidBookingStateException if booking status is not found
     * @should throw InvalidNewBookingStateException if the state flow is invalid
     * @should update the booking state if the request is valid
     */
    @Override
    @Transactional
    public void updateBookingStatusRequest(UpdateBookingStateRequest request) {
        //Check if the booking exists
        Optional<BookingEntity>bookingEntity=bookingRepository.findById(request.getBookingId());
        if (bookingEntity.isEmpty()){
            throw new BookingNotFoundException();
        }
        Booking booking=converter.convertFromBookingEntity(bookingEntity.get());

        //check if requested booking status is valid
        if (!State.isStateValid(request.getBookingState())){
            throw new InvalidBookingStateException();
        }

        State oldState =booking.getState();
        State newState = State.fromStateId(request.getBookingState());

        //check if the old state is modifiable to the new state
        if (!oldState.getNextModifiableState().contains(newState)){
            throw new InvalidNewBookingStateException();
        }

        bookingRepository.updateBookingState(request.getBookingId(), request.getBookingState());

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
