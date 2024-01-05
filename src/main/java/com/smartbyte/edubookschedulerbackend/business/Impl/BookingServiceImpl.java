package com.smartbyte.edubookschedulerbackend.business.Impl;

import com.smartbyte.edubookschedulerbackend.business.BookingService;
import com.smartbyte.edubookschedulerbackend.business.UserService;
import com.smartbyte.edubookschedulerbackend.business.exception.*;
import com.smartbyte.edubookschedulerbackend.business.request.AcceptBookingRequest;
import com.smartbyte.edubookschedulerbackend.business.request.RescheduleBookingRequest;
import com.smartbyte.edubookschedulerbackend.business.request.ScheduleBookingRequest;
import com.smartbyte.edubookschedulerbackend.domain.*;
import com.smartbyte.edubookschedulerbackend.persistence.BookingRequestRepository;
import com.smartbyte.edubookschedulerbackend.persistence.jpa.entity.EntityConverter;
import com.smartbyte.edubookschedulerbackend.business.request.UpdateBookingStateRequest;
import com.smartbyte.edubookschedulerbackend.business.response.GetUpcomingBookingsResponse;
import com.smartbyte.edubookschedulerbackend.persistence.BookingRepository;
import com.smartbyte.edubookschedulerbackend.persistence.UserRepository;
import com.smartbyte.edubookschedulerbackend.persistence.jpa.entity.BookingEntity;
import com.smartbyte.edubookschedulerbackend.persistence.jpa.entity.UserEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {


    private final BookingRepository bookingRepository;
    // TODO: Get rid of userRepository. should use the user service.
    private final UserService userService;
    private final UserRepository userRepository;
    private final EntityConverter converter;
    private final BookingRequestRepository bookingRequestRepo;
    @PersistenceContext
    private EntityManager entityManager;
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
        Optional<UserEntity> user = userRepository.getUserById(studentId);
        if(user.isEmpty()){
            throw new UserNotFoundException();
        }

        LocalDate currentDate = LocalDate.now();
        LocalTime currentTime = LocalTime.now();

        int minutes = currentTime.getHour()*60 + currentTime.getMinute();

        //Fetch the bookings
        List<Booking> bookings = bookingRepository.getUpcomingBookings(
                Date.from(currentDate.atStartOfDay(ZoneId.systemDefault()).toInstant()), user.get()
        ).stream().map(converter::convertFromBookingEntity).toList();

        List<GetUpcomingBookingsResponse> responses = new ArrayList<>();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        //Return list of responses
        for(Booking booking : bookings){
            LocalDate date = booking.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            if ((date.isAfter(currentDate) || (date.equals(currentDate) && booking.getStartTime() > minutes))
                && booking.getState() == State.Requested){
                responses.add(GetUpcomingBookingsResponse.builder()
                        .id(booking.getId())
                        .tutorName(booking.getTutor().getName())
                        .startHour(booking.getStartTime()/60)
                        .startMinute(booking.getStartTime()%60)
                        .date(dateFormat.format(booking.getDate()))
                        .description(booking.getDescription())
                        .build());
            }
        }

        return responses;
    }


    /**
     *
     * @param id Booking's id
     * @return Optional of booking
     *
     * @should return Optional of booking
     */
    @Override
    public Optional<Booking> getBookingById(long id) {
        return bookingRepository.findById(id).map(converter::convertFromBookingEntity);
    }

    /**
     *
     * @param user User
     * @return List of bookings
     *
     * @should return list of bookings
     */
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


    /**
     *
     * @param request ScheduleBookingRequest
     *
     * @should throw ResponseStatusException when requester is not found
     * @should throw ResponseStatusException when receiver is not found
     * @should throw ResponseStatusException when requester is not a student or receiver is not tutor
     * @should save the new booking request
     *
     */
    @Override
    @Transactional
    public void scheduleBooking(ScheduleBookingRequest request) {

        User requester = userService.getUser(request.getRequesterId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNPROCESSABLE_ENTITY,
                        "Requestor doesn't exist"
                        )
                );
        User receiver = userService.getUser(request.getReceiverId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNPROCESSABLE_ENTITY,
                        "Receiver doesn't exist"
                        )
                );
        // requester can only be a student and receiver a tutor for now
        if(!(requester.getRole() == Role.Student && receiver.getRole() == Role.Tutor)){
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    "Requester should be a tutor and receiver a student");
        }


        Booking booking = Booking.builder()
                .date(request.getDate())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .description(request.getDescription())
                .tutor(receiver)
                .student(requester)
                .state(State.Requested)
                .build();
        BookingRequest bookingRequest = BookingRequest.builder()
                .requestType(BookingRequestType.Schedule)
                .requester(requester)
                .receiver(receiver)
                .bookingToSchedule(booking)
                .build();

        bookingRequestRepo.save(converter.convertFromBookingRequest(bookingRequest));
    }

    /**
     *
     * @param request RescheduleBookingRequest
     *
     * @should throw ResponseStatusException when requester is not found
     * @should throw ResponseStatusException when receiver is not found
     * @should throw ResponseStatusException when booking is not found
     * @should throw ResponseStatusException when booking request is not found
     * @should reschedule booking when requester is a tutor
     * @should reschedule booking when requester is a student
     */
    @Override
    @Transactional
    public void rescheduleBooking(RescheduleBookingRequest request) {
        User requester = userService.getUser(request.getRequesterId())
                .orElseThrow(() -> new ResponseStatusException(
                                HttpStatus.UNPROCESSABLE_ENTITY,
                                "Requester doesn't exist"
                        )
                );
        User receiver = userService.getUser(request.getReceiverId())
                .orElseThrow(() -> new ResponseStatusException(
                                HttpStatus.UNPROCESSABLE_ENTITY,
                                "Receiver doesn't exist"
                        )
                );
        Booking rescheduledBooking = getBookingById(request.getRescheduledBookingId())
                .orElseThrow(() -> new ResponseStatusException(
                                HttpStatus.UNPROCESSABLE_ENTITY,
                                "Booking doesn't exist"
                        )
                );

        State bookingState = rescheduledBooking.getState();
        if(bookingState == State.Requested || bookingState == State.Reschedule_Requested){
            BookingRequest prevSchedRequest = bookingRequestRepo.findPreviousRequest(
                    converter.convertFromUser(requester),
                    converter.convertFromUser(receiver),
                    converter.convertFromBooking(rescheduledBooking)
            )
                    .map(converter::convertFromBookingRequestEntity)
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.INTERNAL_SERVER_ERROR,
                            "Invalid state: booking without request"
                    ));

            bookingRequestRepo.updateAnswer(prevSchedRequest.getId(), BookingRequestAnswer.Rejected);
        }

        updateBookingState(rescheduledBooking.getId(), State.Reschedule_Requested);

        Booking.BookingBuilder bookingBuilder = Booking.builder()
                .date(request.getDate())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .description(request.getDescription())
                .state(State.Reschedule_Wait_Accept);

        if(requester.getRole() == Role.Student){
            bookingBuilder.student(requester);
            bookingBuilder.tutor(receiver);
        } else if (requester.getRole() == Role.Tutor) {
            bookingBuilder.student(receiver);
            bookingBuilder.tutor(requester);
        }

        Booking schedBooking = bookingBuilder.build();
        BookingRequest bookingRequest = BookingRequest.builder()
                .requestType(BookingRequestType.Reschedule)
                .requester(requester)
                .receiver(receiver)
                .bookingToSchedule(schedBooking)
                .bookingToReschedule(rescheduledBooking)
                .build();

        bookingRequestRepo.save(converter.convertFromBookingRequest(bookingRequest));


    }

    /**
     *
     * @param request AcceptBookingRequest
     *
     * @should throw BookingRequestNotFoundException when booking request is not found
     * @should throw ResponseStatusException when new answer is invalid
     * @should throw ResponseStatusException when booking request answer is not no answer
     * @should throw ResponseStatusException when new answer is no answer
     * @should throw ResponseStatusException when new answer is accepted, booking request's type is schedule, and booking state is not requested
     * @should throw ResponseStatusException when new answer is accepted, booking request's type is reschedule, and booking state is not reschedule requested
     * @should throw ResponseStatusException when new answer is accepted, booking request's type is reschedule, and rescheduled booking state is not reshedule wait accept
     * @should accept booking when new answer is accepted and booking request's type is schedule
     * @should accept booking when new answer is accepted and booking request's type is reschedule
     * @should cancel booking when new answer is rejected
     * @should cancel rescheduled booking when new answer is rejected and booking request has rescheduled a booking
     */
    @Override
    @Transactional
    public void acceptBooking(AcceptBookingRequest request) {
        BookingRequest schedBookingRequest = this.getBookingRequestById(request.getBookingRequestId())
                .orElseThrow(BookingRequestNotFoundException::new);

        BookingRequestAnswer answer;
        BookingRequestAnswer[] answerValues =  BookingRequestAnswer.values();
        int ansId = request.getAnswer();
        if(!(0 <= ansId && ansId < answerValues.length)){
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY);
        }
        answer = answerValues[ansId];

        if(schedBookingRequest.getAnswer() != BookingRequestAnswer.NoAnswer)
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    "Booking request already answered");
        if(answer == BookingRequestAnswer.NoAnswer)
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    "Can't answer NoAnswer");

        bookingRequestRepo.updateAnswer(schedBookingRequest.getId(), answer);

        if(answer == BookingRequestAnswer.Accepted){
            switch(schedBookingRequest.getRequestType()){
                case Schedule:
                    if(schedBookingRequest.getBookingToSchedule().getState() != State.Requested)
                        throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                                "Schedule request on non-Requested booking");
                    updateBookingState(schedBookingRequest.getBookingToSchedule().getId(), State.Scheduled);
                    break;
                case Reschedule:
                    if(schedBookingRequest.getBookingToReschedule().getState() != State.Reschedule_Requested)
                        throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                                "Reschedule request on non-Reschedule_Requested booking");
                    if(schedBookingRequest.getBookingToSchedule().getState() != State.Reschedule_Wait_Accept)
                        throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                                "scheduled booking for rescheduling not in Reschedule_Wait_accept state");
                    updateBookingState(schedBookingRequest.getBookingToReschedule().getId(), State.Rescheduled);
                    updateBookingState(schedBookingRequest.getBookingToSchedule().getId(), State.Scheduled);
                    break;
            }
        } else if (answer == BookingRequestAnswer.Rejected) {
            updateBookingState(schedBookingRequest.getBookingToSchedule().getId(), State.Cancelled);
            Booking reschedBooking = schedBookingRequest.getBookingToReschedule();
            if(reschedBooking != null){
                updateBookingState(reschedBooking.getId(), State.Cancelled);
            }
        }


    }

    /**
     *
     * @param request UpdateBookingStateRequest
     *
     * @should update the booking state to cancelled
     */
    @Override
    @Transactional
    public void cancelBooking(UpdateBookingStateRequest request) {
        updateBookingState(request.getBookingId(),State.Cancelled);
    }

    /**
     *
     * @param request UpdateBookingStateRequest
     *
     * @should update the booking state to finished
     */
    @Override
    @Transactional
    public void finishBooking(UpdateBookingStateRequest request) {
        updateBookingState(request.getBookingId(),State.Finished);
    }

    @Override
    public Optional<BookingRequest> getBookingRequestById(long id) {
         return bookingRequestRepo.findById(id)
                 .map(converter::convertFromBookingRequestEntity);
    }

    /**
     *
     * @param user User
     * @return List of booking requests
     *
     * @should return list of booking requests
     */
    @Override
    public List<BookingRequest> getSentBookingRequests(User user) {
        return bookingRequestRepo.findBookingRequestsEntitiesByRequester(converter.convertFromUser(user))
                .stream().map(converter::convertFromBookingRequestEntity).toList();
    }

    /**
     *
     * @param user User
     * @return List of booking requests
     *
     * @should return list of booking requests
     */
    @Override
    public List<BookingRequest> getReceivedBookingRequests(User user) {
        return bookingRequestRepo.findBookingRequestsEntitiesByReceiver(converter.convertFromUser(user))
                .stream().map(converter::convertFromBookingRequestEntity).toList();
    }

    /**
     *
     * @param bookingId booking id
     * @param newState new booking state
     *
     * @should throw BookingNotFoundException when booking is not found
     * @should throw InvalidNewBookingStateException when booking cannot be changed to the new state
     */
    @Transactional
    public void updateBookingState(long bookingId,State newState) {
        //Check if the booking exists
        Optional<BookingEntity> bookingEntity = bookingRepository.findById(bookingId);
        if (bookingEntity.isEmpty()){
            throw new BookingNotFoundException();
        }
        Booking booking = converter.convertFromBookingEntity(bookingEntity.get());

        State oldState = booking.getState();

        List<Role> allowedRoles = oldState.getNextModifiableState().get(newState);

        //check if the old state is modifiable to the new state
        //TODO change validations to user's role
        if (allowedRoles == null){
            throw new InvalidNewBookingStateException();
        }

        bookingRepository.updateBookingState(bookingId, newState.getStateId());
    }

}
