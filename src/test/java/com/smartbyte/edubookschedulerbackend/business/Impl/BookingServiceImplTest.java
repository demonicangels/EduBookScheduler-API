package com.smartbyte.edubookschedulerbackend.business.Impl;

import com.smartbyte.edubookschedulerbackend.business.UserService;
import com.smartbyte.edubookschedulerbackend.business.exception.BookingNotFoundException;
import com.smartbyte.edubookschedulerbackend.business.exception.BookingRequestNotFoundException;
import com.smartbyte.edubookschedulerbackend.business.exception.InvalidNewBookingStateException;
import com.smartbyte.edubookschedulerbackend.business.request.AcceptBookingRequest;
import com.smartbyte.edubookschedulerbackend.business.request.RescheduleBookingRequest;
import com.smartbyte.edubookschedulerbackend.business.request.ScheduleBookingRequest;
import com.smartbyte.edubookschedulerbackend.business.request.UpdateBookingStateRequest;
import com.smartbyte.edubookschedulerbackend.domain.*;
import com.smartbyte.edubookschedulerbackend.persistence.BookingRequestRepository;
import com.smartbyte.edubookschedulerbackend.persistence.jpa.entity.BookingRequestEntity;
import com.smartbyte.edubookschedulerbackend.persistence.jpa.entity.EntityConverter;
import com.smartbyte.edubookschedulerbackend.business.exception.UserNotFoundException;
import com.smartbyte.edubookschedulerbackend.business.response.GetUpcomingBookingsResponse;
import com.smartbyte.edubookschedulerbackend.persistence.BookingRepository;
import com.smartbyte.edubookschedulerbackend.persistence.UserRepository;
import com.smartbyte.edubookschedulerbackend.persistence.jpa.entity.BookingEntity;
import com.smartbyte.edubookschedulerbackend.persistence.jpa.entity.UserEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
 class BookingServiceImplTest {
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private BookingRequestRepository bookingRequestRepository;
    @Mock
    private UserRepository userRepository;

    @Mock
    private UserService userService;
    @Mock
    private EntityConverter converter;

    @InjectMocks
    private BookingServiceImpl bookingService;


    /**
     * @verifies throw UserNotFoundException when student is not found
     * @see BookingServiceImpl#getUpcomingBookings(long)
     */
    @Test
    void getUpcomingBookings_shouldThrowUserNotFoundExceptionWhenStudentIsNotFound() {
        //Arrange
        when(userRepository.getUserById(1L)).thenReturn(Optional.empty());

        //Act + Assert
        assertThrows(UserNotFoundException.class ,()->bookingService.getUpcomingBookings(1L));

    }

    /**
     * @verifies return an empty list when there is no upcoming booking
     * @see BookingServiceImpl#getUpcomingBookings(long)
     */
    @Test
    void getUpcomingBookings_shouldReturnAnEmptyListWhenThereIsNoUpcomingBooking() {
        //Arrange
        UserEntity user=UserEntity.builder()
                .id(1L)
                .build();

        when(userRepository.getUserById(user.getId())).thenReturn(Optional.of(user));

        LocalDate currentDate=LocalDate.now();

        when(bookingRepository.getUpcomingBookings(
                Date.from(currentDate.atStartOfDay(ZoneId.systemDefault()).toInstant()), user)
        ).thenReturn(List.of());

        //Act
        List<GetUpcomingBookingsResponse>responses=bookingService.getUpcomingBookings(user.getId());

        //Assert
        assert responses.isEmpty();

    }

    /**
     * @verifies return a list of bookings when there are upcoming bookings
     * @see BookingServiceImpl#getUpcomingBookings(long)
     */
    @Test
    void getUpcomingBookings_shouldReturnAListOfBookingsWhenThereAreUpcomingBookings() {
        //Arrange
        UserEntity student=UserEntity.builder()
                .id(1L)
                .role(0)
                .build();

        when(userRepository.getUserById(student.getId())).thenReturn(Optional.of(student));

        UserEntity tutorEntity=UserEntity.builder()
                .id(2L)
                .role(1)
                .name("tutor")
                .build();

        Tutor tutor=Tutor.builder()
                .id(2L)
                .role(Role.Tutor)
                .name("tutor")
                .build();
        LocalDate currentDate=LocalDate.now();

        List<BookingEntity>bookings=List.of(BookingEntity.builder()
                .id(1L)
                .tutor(tutorEntity)
                .startTime(1439)
                .description("meeting")
                .date(new Date())
                .build(),
                BookingEntity.builder()
                        .id(2L)
                        .tutor(tutorEntity)
                        .startTime(1439)
                        .description("meeting 2")
                        .date(new Date())
                        .build()
        );

        when(bookingRepository.getUpcomingBookings(
                Date.from(currentDate.atStartOfDay(ZoneId.systemDefault()).toInstant()), student)
        ).thenReturn(bookings);

        when(converter.convertFromBookingEntity(bookings.get(0))).thenReturn(Booking.builder()
                .id(bookings.get(0).getId())
                .description(bookings.get(0).getDescription())
                .tutor(tutor)
                .startTime(bookings.get(0).getStartTime())
                .date(bookings.get(0).getDate())
                .build()
        );
        when(converter.convertFromBookingEntity(bookings.get(1))).thenReturn(Booking.builder()
                .id(bookings.get(1).getId())
                .description(bookings.get(1).getDescription())
                .tutor(tutor)
                .date(bookings.get(1).getDate())
                .startTime(bookings.get(1).getStartTime())
                .build()
        );

        SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");

        List<GetUpcomingBookingsResponse>expectedResponses=bookings.stream().map(
                booking->GetUpcomingBookingsResponse.builder()
                        .description(booking.getDescription())
                        .startMinute(booking.getStartTime()%60)
                        .startHour(booking.getStartTime()/60)
                        .tutorName(tutor.getName())
                        .date(dateFormat.format(booking.getDate()))
                        .id(booking.getId())
                        .build()
        ).toList();

        //Act
        List<GetUpcomingBookingsResponse>actualResponses=bookingService.getUpcomingBookings(student.getId());

        //Assert
        assertEquals(expectedResponses,actualResponses);
    }


    /**
     * @verifies return list of bookings
     * @see BookingServiceImpl#getUsersBooking(com.smartbyte.edubookschedulerbackend.domain.User)
     */
    @ParameterizedTest
    @EnumSource(value = Role.class,names = {"Tutor","Student"})
    void getUsersBooking_shouldReturnListOfBookings(Role role) {
        //Arrange
        User user=Admin.builder().build();

        UserEntity userEntity=UserEntity.builder()
                .id(1L)
                .role(role.getRoleId())
                .build();

        List<Booking>expectedBookings=new ArrayList<>();


        switch (role) {
            case Tutor -> {
                user = Tutor.builder()
                        .id(userEntity.getId())
                        .role(role)
                        .build();
                when(converter.convertFromUser(user)).thenReturn(userEntity);

                List<BookingEntity>bookingEntities=List.of(
                        BookingEntity.builder()
                                .id(1L)
                                .tutor(userEntity)
                                .build(),
                        BookingEntity.builder()
                                .id(2L)
                                .tutor(userEntity)
                                .build()
                );

                when(bookingRepository.findByTutor(userEntity)).thenReturn(bookingEntities);
                for (BookingEntity bookingEntity:bookingEntities){
                    Tutor tutor=Tutor.builder()
                            .id(bookingEntity.getTutor().getId())
                            .role(Role.fromRoleId(bookingEntity.getTutor().getRole()))
                            .build();

                    Booking booking=Booking.builder()
                            .id(bookingEntity.getId())
                            .tutor(tutor)
                            .build();

                    when(converter.convertFromBookingEntity(bookingEntity)).thenReturn(booking);

                    expectedBookings.add(booking);

                }
            }
            case Student -> {
                user=Student.builder()
                        .id(userEntity.getId())
                        .role(role)
                        .build();
                when(converter.convertFromUser(user)).thenReturn(userEntity);
                List<BookingEntity>bookingEntities=List.of(
                        BookingEntity.builder()
                                .id(1L)
                                .student(userEntity)
                                .build(),
                        BookingEntity.builder()
                                .id(2L)
                                .student(userEntity)
                                .build()
                );

                when(bookingRepository.findByStudent(userEntity)).thenReturn(bookingEntities);
                for (BookingEntity bookingEntity:bookingEntities){
                    Student student=Student.builder()
                            .id(bookingEntity.getStudent().getId())
                            .role(Role.fromRoleId(bookingEntity.getStudent().getRole()))
                            .build();

                    Booking booking=Booking.builder()
                            .id(bookingEntity.getId())
                            .student(student)
                            .build();

                    when(converter.convertFromBookingEntity(bookingEntity)).thenReturn(booking);

                    expectedBookings.add(booking);

                }

                when(bookingRepository.findByStudent(userEntity)).thenReturn(bookingEntities);
            }
        }

        //Act
        List<Booking>actualBookings=bookingService.getUsersBooking(user);

        //Assert
        assertEquals(expectedBookings,actualBookings);

    }

    /**
     * @verifies return Optional of booking
     * @see BookingServiceImpl#getBookingById(long)
     */
    @Test
    void getBookingById_shouldReturnOptionalOfBooking() {
        //Arrange

        BookingEntity bookingEntity=BookingEntity.builder()
                .id(1L)
                .build();

        Booking booking=Booking.builder()
                .id(bookingEntity.getId())
                .build();

        when(bookingRepository.findById(bookingEntity.getId())).thenReturn(Optional.of(bookingEntity));

        when(converter.convertFromBookingEntity(bookingEntity)).thenReturn(booking);

        Optional<Booking>expectedBooking=Optional.of(booking);

        //Act

        Optional<Booking>actualBooking=bookingService.getBookingById(booking.getId());

        //Assert
        assertEquals(expectedBooking,actualBooking);

    }

    /**
     * @verifies return list of booking requests
     * @see BookingServiceImpl#getReceivedBookingRequests(User)
     */
    @Test
    void getReceivedBookingRequests_shouldReturnListOfBookingRequests() {
        //Arrange
        User user=Tutor.builder()
                .id(1L)
                .role(Role.Tutor)
                .build();

        List<BookingRequestEntity> bookingRequestEntities=List.of(
                BookingRequestEntity.builder()
                        .id(1L)
                        .build()
        );

        when(bookingRequestRepository.findBookingRequestsEntitiesByReceiver(converter.convertFromUser(user)))
                .thenReturn(bookingRequestEntities);

        List<BookingRequest> expectedBookingRequests=new ArrayList<>();

        for (BookingRequestEntity bookingRequestEntity:bookingRequestEntities){

            BookingRequest bookingRequest=BookingRequest.builder()
                    .id(bookingRequestEntity.getId())
                    .build();

            when(converter.convertFromBookingRequestEntity(bookingRequestEntity)).thenReturn(bookingRequest);

            expectedBookingRequests.add(bookingRequest);

        }

        //Act

        List<BookingRequest>actualBookingRequests=bookingService.getReceivedBookingRequests(user);

        //Assert

        assertEquals(expectedBookingRequests,actualBookingRequests);
    }

    /**
     * @verifies return list of booking requests
     * @see BookingServiceImpl#getSentBookingRequests(User)
     */
    @Test
    void getSentBookingRequests_shouldReturnListOfBookingRequests() {
        //Arrange
        User user=Tutor.builder()
                .id(1L)
                .role(Role.Tutor)
                .build();

        List<BookingRequestEntity> bookingRequestEntities=List.of(
                BookingRequestEntity.builder()
                        .id(1L)
                        .build()
        );

        when(bookingRequestRepository.findBookingRequestsEntitiesByRequester(converter.convertFromUser(user)))
                .thenReturn(bookingRequestEntities);

        List<BookingRequest> expectedBookingRequests=new ArrayList<>();

        for (BookingRequestEntity bookingRequestEntity:bookingRequestEntities){

            BookingRequest bookingRequest=BookingRequest.builder()
                    .id(bookingRequestEntity.getId())
                    .build();

            when(converter.convertFromBookingRequestEntity(bookingRequestEntity)).thenReturn(bookingRequest);

            expectedBookingRequests.add(bookingRequest);

        }

        //Act

        List<BookingRequest>actualBookingRequests=bookingService.getSentBookingRequests(user);

        //Assert

        assertEquals(expectedBookingRequests,actualBookingRequests);

    }

    /**
     * @verifies throw BookingNotFoundException when booking is not found
     * @see BookingServiceImpl#updateBookingState(long, State)
     */
    @Test
    void updateBookingState_shouldThrowBookingNotFoundExceptionWhenBookingIsNotFound() {
        //Arrange
        when(bookingRepository.findById(1L)).thenReturn(Optional.empty());

        //Act + Assert
        assertThrows(BookingNotFoundException.class,()->bookingService.updateBookingState(1L,State.Requested));

    }

    /**
     * @verifies throw InvalidNewBookingStateException when booking cannot be changed to the new state
     * @see BookingServiceImpl#updateBookingState(long, State)
     */
    @Test
    void updateBookingState_shouldThrowInvalidNewBookingStateExceptionWhenBookingCannotBeChangedToTheNewState() {
        //Arrange
        BookingEntity bookingEntity= BookingEntity.builder()
                .id(1L)
                .state(State.Finished.getStateId())
                .build();

        when(bookingRepository.findById(bookingEntity.getId())).thenReturn(Optional.of(bookingEntity));

        Booking booking=Booking.builder()
                .state(State.fromStateId(bookingEntity.getState()))
                .id(bookingEntity.getId())
                .build();

        when(converter.convertFromBookingEntity(bookingEntity)).thenReturn(booking);

        //Act + Assert
        assertThrows(InvalidNewBookingStateException.class,()->bookingService.updateBookingState(1L,State.Requested));
    }

    /**
     * @verifies update the booking state to finished
     * @see BookingServiceImpl#finishBooking(com.smartbyte.edubookschedulerbackend.business.request.UpdateBookingStateRequest)
     */
    @Test
    void finishBooking_shouldUpdateTheBookingStateToFinished() {
        //Arrange
        UpdateBookingStateRequest request=UpdateBookingStateRequest.builder()
                .bookingId(1L)
                .build();

        BookingEntity bookingEntity= BookingEntity.builder()
                .id(request.getBookingId())
                .state(State.Scheduled.getStateId())
                .build();

        when(bookingRepository.findById(bookingEntity.getId())).thenReturn(Optional.of(bookingEntity));

        Booking booking=Booking.builder()
                .state(State.fromStateId(bookingEntity.getState()))
                .id(bookingEntity.getId())
                .build();

        when(converter.convertFromBookingEntity(bookingEntity)).thenReturn(booking);

        //Act
        bookingService.finishBooking(request);

        //Assert
        verify(bookingRepository).updateBookingState(request.getBookingId(),State.Finished.getStateId());

    }

    /**
     * @verifies update the booking state to cancelled
     * @see BookingServiceImpl#cancelBooking(UpdateBookingStateRequest)
     */
    @Test
    void cancelBooking_shouldUpdateTheBookingStateToCancelled() {
        //Arrange
        UpdateBookingStateRequest request=UpdateBookingStateRequest.builder()
                .bookingId(1L)
                .build();

        BookingEntity bookingEntity= BookingEntity.builder()
                .id(request.getBookingId())
                .state(State.Scheduled.getStateId())
                .build();

        when(bookingRepository.findById(bookingEntity.getId())).thenReturn(Optional.of(bookingEntity));

        Booking booking=Booking.builder()
                .state(State.fromStateId(bookingEntity.getState()))
                .id(bookingEntity.getId())
                .build();

        when(converter.convertFromBookingEntity(bookingEntity)).thenReturn(booking);

        //Act
        bookingService.cancelBooking(request);

        //Assert
        verify(bookingRepository).updateBookingState(request.getBookingId(),State.Cancelled.getStateId());
    }

    /**
     * @verifies throw ResponseStatusException when requester is not found
     * @see BookingServiceImpl#scheduleBooking(com.smartbyte.edubookschedulerbackend.business.request.ScheduleBookingRequest)
     */
    @Test
    void scheduleBooking_shouldThrowResponseStatusExceptionWhenRequesterIsNotFound() {
        //Arrange
        ScheduleBookingRequest request=ScheduleBookingRequest.builder()
                .requesterId(1L)
                .endTime(2)
                .receiverId(2L)
                .startTime(1)
                .description("booking")
                .date(new Date())
                .build();

        when(userService.getUser(request.getRequesterId())).thenReturn(Optional.empty());

        //Act + Assert
        assertThrows(ResponseStatusException.class,()->bookingService.scheduleBooking(request));

    }

    /**
     * @verifies throw ResponseStatusException when receiver is not found
     * @see BookingServiceImpl#scheduleBooking(com.smartbyte.edubookschedulerbackend.business.request.ScheduleBookingRequest)
     */
    @Test
    void scheduleBooking_shouldThrowResponseStatusExceptionWhenReceiverIsNotFound() {
        //Arrange
        ScheduleBookingRequest request=ScheduleBookingRequest.builder()
                .requesterId(1L)
                .endTime(2)
                .receiverId(2L)
                .startTime(1)
                .description("booking")
                .date(new Date())
                .build();

        User requester=Student.builder()
                .id(request.getRequesterId())
                .role(Role.Student)
                .build();

        when(userService.getUser(request.getRequesterId())).thenReturn(Optional.of(requester));

        when(userService.getUser(request.getReceiverId())).thenReturn(Optional.empty());

        //Act + Assert
        assertThrows(ResponseStatusException.class,()->bookingService.scheduleBooking(request));
    }

    /**
     * @verifies throw ResponseStatusException when requester is not a student or receiver is not tutor
     * @see BookingServiceImpl#scheduleBooking(com.smartbyte.edubookschedulerbackend.business.request.ScheduleBookingRequest)
     */
    @Test
    void scheduleBooking_shouldThrowResponseStatusExceptionWhenRequesterIsNotAStudentOrReceiverIsNotTutor() {
        //Arrange
        ScheduleBookingRequest request=ScheduleBookingRequest.builder()
                .requesterId(1L)
                .endTime(2)
                .receiverId(2L)
                .startTime(1)
                .description("booking")
                .date(new Date())
                .build();

        User requester=Tutor.builder()
                .id(request.getRequesterId())
                .role(Role.Tutor)
                .build();

        User receiver=Student.builder()
                .id(request.getReceiverId())
                .role(Role.Student)
                .build();

        when(userService.getUser(request.getRequesterId())).thenReturn(Optional.of(requester));

        when(userService.getUser(request.getReceiverId())).thenReturn(Optional.of(receiver));

        //Act + Assert
        assertThrows(ResponseStatusException.class,()->bookingService.scheduleBooking(request));


    }

    /**
     * @verifies save the new booking request
     * @see BookingServiceImpl#scheduleBooking(com.smartbyte.edubookschedulerbackend.business.request.ScheduleBookingRequest)
     */
    @Test
    void scheduleBooking_shouldSaveTheNewBookingRequest() {
        //Arrange
        ScheduleBookingRequest request=ScheduleBookingRequest.builder()
                .requesterId(1L)
                .endTime(2)
                .receiverId(2L)
                .startTime(1)
                .description("booking")
                .date(new Date())
                .build();

        User requester=Student.builder()
                .id(request.getRequesterId())
                .build();

        User receiver=Tutor.builder()
                .id(request.getReceiverId())
                .build();

        when(userService.getUser(request.getRequesterId())).thenReturn(Optional.of(requester));

        when(userService.getUser(request.getReceiverId())).thenReturn(Optional.of(receiver));

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

        //Act
        bookingService.scheduleBooking(request);

        //Assert
        verify(bookingRequestRepository).save(converter.convertFromBookingRequest(bookingRequest));

    }

    /**
     * @verifies throw ResponseStatusException when requester is not found
     * @see BookingServiceImpl#rescheduleBooking(com.smartbyte.edubookschedulerbackend.business.request.RescheduleBookingRequest)
     */
    @Test
    void rescheduleBooking_shouldThrowResponseStatusExceptionWhenRequesterIsNotFound() {
        //Arrange
        RescheduleBookingRequest request=RescheduleBookingRequest.builder()
                .requesterId(1L)
                .rescheduledBookingId(1L)
                .endTime(2)
                .receiverId(2L)
                .startTime(1)
                .date(new Date())
                .build();

        when(userService.getUser(request.getRequesterId())).thenReturn(Optional.empty());

        //Act + Assert
        assertThrows(ResponseStatusException.class,()->bookingService.rescheduleBooking(request));

    }

    /**
     * @verifies throw ResponseStatusException when receiver is not found
     * @see BookingServiceImpl#rescheduleBooking(com.smartbyte.edubookschedulerbackend.business.request.RescheduleBookingRequest)
     */
    @Test
    void rescheduleBooking_shouldThrowResponseStatusExceptionWhenReceiverIsNotFound() {
        //Arrange
        RescheduleBookingRequest request=RescheduleBookingRequest.builder()
                .requesterId(1L)
                .rescheduledBookingId(1L)
                .endTime(2)
                .receiverId(2L)
                .startTime(1)
                .date(new Date())
                .build();

        User requester=Tutor.builder()
                .id(request.getRequesterId())
                .build();

        when(userService.getUser(request.getRequesterId())).thenReturn(Optional.of(requester));

        when(userService.getUser(request.getReceiverId())).thenReturn(Optional.empty());


        //Act + Assert
        assertThrows(ResponseStatusException.class,()->bookingService.rescheduleBooking(request));
    }

    /**
     * @verifies throw ResponseStatusException when booking is not found
     * @see BookingServiceImpl#rescheduleBooking(com.smartbyte.edubookschedulerbackend.business.request.RescheduleBookingRequest)
     */
    @Test
    void rescheduleBooking_shouldThrowResponseStatusExceptionWhenBookingIsNotFound() {
        //Arrange
        RescheduleBookingRequest request=RescheduleBookingRequest.builder()
                .requesterId(1L)
                .rescheduledBookingId(1L)
                .endTime(2)
                .receiverId(2L)
                .startTime(1)
                .date(new Date())
                .build();

        User requester=Tutor.builder()
                .id(request.getRequesterId())
                .build();

        User receiver=Tutor.builder()
                .id(request.getRequesterId())
                .build();

        when(userService.getUser(request.getRequesterId())).thenReturn(Optional.of(requester));

        when(userService.getUser(request.getReceiverId())).thenReturn(Optional.of(receiver));

        when(bookingRepository.findById(request.getRescheduledBookingId())).thenReturn(Optional.empty());


        //Act + Assert
        assertThrows(ResponseStatusException.class,()->bookingService.rescheduleBooking(request));
    }

    /**
     * @verifies throw ResponseStatusException when booking request is not found
     * @see BookingServiceImpl#rescheduleBooking(com.smartbyte.edubookschedulerbackend.business.request.RescheduleBookingRequest)
     */
    @Test
    void rescheduleBooking_shouldThrowResponseStatusExceptionWhenBookingRequestIsNotFound() {
        //Arrange
        RescheduleBookingRequest request=RescheduleBookingRequest.builder()
                .requesterId(1L)
                .rescheduledBookingId(1L)
                .endTime(2)
                .receiverId(2L)
                .startTime(1)
                .date(new Date())
                .build();

        UserEntity requesterEntity=UserEntity.builder()
                .id(request.getRequesterId())
                .role(Role.Tutor.getRoleId())
                .build();

        User requester=Tutor.builder()
                .id(request.getRequesterId())
                .build();

        UserEntity receiverEntity=UserEntity.builder()
                .id(request.getRequesterId())
                .role(Role.Tutor.getRoleId())
                .build();

        User receiver=Tutor.builder()
                .id(request.getRequesterId())
                .build();

        BookingEntity bookingEntity=BookingEntity.builder()
                .id(1L)
                .tutor(requesterEntity)
                .student(receiverEntity)
                .state(State.Requested.getStateId())
                .build();

        Booking booking=Booking.builder()
                .id(1L)
                .tutor(requester)
                .student(receiver)
                .state(State.fromStateId(bookingEntity.getState()))
                .build();

        when(userService.getUser(request.getRequesterId())).thenReturn(Optional.of(requester));

        when(userService.getUser(request.getReceiverId())).thenReturn(Optional.of(receiver));

        when(bookingRepository.findById(request.getRescheduledBookingId())).thenReturn(Optional.of(bookingEntity));

        when(converter.convertFromBookingEntity(bookingEntity)).thenReturn(booking);

        when(bookingRequestRepository.findPreviousRequest(
                converter.convertFromUser(receiver),
                converter.convertFromUser(requester),
                converter.convertFromBooking(booking)
        )).thenReturn(Optional.empty());

        //Act + Assert
        assertThrows(ResponseStatusException.class,()->bookingService.rescheduleBooking(request));
    }

    /**
     * @verifies reschedule booking when requester is a tutor
     * @see BookingServiceImpl#rescheduleBooking(RescheduleBookingRequest)
     */
    @Test
    void rescheduleBooking_shouldRescheduleBookingWhenRequesterIsATutor() {
        //Arrange
        RescheduleBookingRequest request=RescheduleBookingRequest.builder()
                .requesterId(1L)
                .rescheduledBookingId(1L)
                .endTime(2)
                .receiverId(2L)
                .startTime(1)
                .date(new Date())
                .build();

        UserEntity requesterEntity=UserEntity.builder()
                .id(request.getRequesterId())
                .role(Role.Tutor.getRoleId())
                .build();

        User requester=Tutor.builder()
                .id(request.getRequesterId())
                .build();

        UserEntity receiverEntity=UserEntity.builder()
                .id(request.getRequesterId())
                .role(Role.Student.getRoleId())
                .build();

        User receiver=Student.builder()
                .id(request.getRequesterId())
                .build();

        BookingEntity bookingEntity=BookingEntity.builder()
                .id(1L)
                .tutor(requesterEntity)
                .student(receiverEntity)
                .state(State.Requested.getStateId())
                .build();

        Booking booking=Booking.builder()
                .id(1L)
                .tutor(requester)
                .student(receiver)
                .state(State.fromStateId(bookingEntity.getState()))
                .build();

        when(userService.getUser(request.getRequesterId())).thenReturn(Optional.of(requester));

        when(userService.getUser(request.getReceiverId())).thenReturn(Optional.of(receiver));

        when(bookingRepository.findById(request.getRescheduledBookingId())).thenReturn(Optional.of(bookingEntity));

        when(converter.convertFromBookingEntity(bookingEntity)).thenReturn(booking);

        BookingRequestEntity bookingRequestEntity=BookingRequestEntity.builder()
                .id(1L)
                .bookingToReschedule(bookingEntity)
                .requester(requesterEntity)
                .receiver(receiverEntity)
                .build();

        when(bookingRequestRepository.findPreviousRequest(
                converter.convertFromUser(receiver),
                converter.convertFromUser(requester),
                converter.convertFromBooking(booking)
        )).thenReturn(Optional.of(bookingRequestEntity));

        BookingRequest bookingRequest=BookingRequest.builder()
                .id(bookingRequestEntity.getId())
                .bookingToReschedule(booking)
                .requester(requester)
                .receiver(receiver)
                .build();

        when(converter.convertFromBookingRequestEntity(bookingRequestEntity)).thenReturn(bookingRequest);

        Booking newBooking=Booking.builder()
                .date(request.getDate())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .description(request.getDescription())
                .state(State.Reschedule_Wait_Accept)
                .tutor(requester)
                .student(receiver)
                .build();

        BookingRequest newBookingRequest=BookingRequest.builder()
                .requestType(BookingRequestType.Reschedule)
                .requester(requester)
                .receiver(receiver)
                .bookingToSchedule(newBooking)
                .bookingToReschedule(booking)
                .build();

        //Act
        bookingService.rescheduleBooking(request);

        //Assert
        verify(bookingRequestRepository).updateAnswer(bookingRequest.getId(),BookingRequestAnswer.Rejected);
        verify(bookingRepository).updateBookingState(booking.getId(),State.Reschedule_Requested.getStateId());
        verify(bookingRequestRepository).save(converter.convertFromBookingRequest(newBookingRequest));

    }

    /**
     * @verifies reschedule booking when requester is a student
     * @see BookingServiceImpl#rescheduleBooking(RescheduleBookingRequest)
     */
    @Test
    void rescheduleBooking_shouldRescheduleBookingWhenRequesterIsAStudent() {
        //Arrange
        RescheduleBookingRequest request=RescheduleBookingRequest.builder()
                .requesterId(1L)
                .rescheduledBookingId(1L)
                .endTime(2)
                .receiverId(2L)
                .startTime(1)
                .date(new Date())
                .build();

        UserEntity requesterEntity=UserEntity.builder()
                .id(request.getRequesterId())
                .role(Role.Student.getRoleId())
                .build();

        User requester=Student.builder()
                .id(request.getRequesterId())
                .build();

        UserEntity receiverEntity=UserEntity.builder()
                .id(request.getRequesterId())
                .role(Role.Tutor.getRoleId())
                .build();

        User receiver=Tutor.builder()
                .id(request.getRequesterId())
                .build();

        BookingEntity bookingEntity=BookingEntity.builder()
                .id(1L)
                .tutor(requesterEntity)
                .student(receiverEntity)
                .state(State.Requested.getStateId())
                .build();

        Booking booking=Booking.builder()
                .id(1L)
                .tutor(requester)
                .student(receiver)
                .state(State.fromStateId(bookingEntity.getState()))
                .build();

        when(userService.getUser(request.getRequesterId())).thenReturn(Optional.of(requester));

        when(userService.getUser(request.getReceiverId())).thenReturn(Optional.of(receiver));

        when(bookingRepository.findById(request.getRescheduledBookingId())).thenReturn(Optional.of(bookingEntity));

        when(converter.convertFromBookingEntity(bookingEntity)).thenReturn(booking);

        BookingRequestEntity bookingRequestEntity=BookingRequestEntity.builder()
                .id(1L)
                .bookingToReschedule(bookingEntity)
                .requester(requesterEntity)
                .receiver(receiverEntity)
                .build();

        when(bookingRequestRepository.findPreviousRequest(
                converter.convertFromUser(receiver),
                converter.convertFromUser(requester),
                converter.convertFromBooking(booking)
        )).thenReturn(Optional.of(bookingRequestEntity));

        BookingRequest bookingRequest=BookingRequest.builder()
                .id(bookingRequestEntity.getId())
                .bookingToReschedule(booking)
                .requester(requester)
                .receiver(receiver)
                .build();

        when(converter.convertFromBookingRequestEntity(bookingRequestEntity)).thenReturn(bookingRequest);

        Booking newBooking=Booking.builder()
                .date(request.getDate())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .description(request.getDescription())
                .state(State.Reschedule_Wait_Accept)
                .tutor(receiver)
                .student(requester)
                .build();

        BookingRequest newBookingRequest=BookingRequest.builder()
                .requestType(BookingRequestType.Reschedule)
                .requester(requester)
                .receiver(receiver)
                .bookingToSchedule(newBooking)
                .bookingToReschedule(booking)
                .build();

        //Act
        bookingService.rescheduleBooking(request);

        //Assert
        verify(bookingRequestRepository).updateAnswer(bookingRequest.getId(),BookingRequestAnswer.Rejected);
        verify(bookingRepository).updateBookingState(booking.getId(),State.Reschedule_Requested.getStateId());
        verify(bookingRequestRepository).save(converter.convertFromBookingRequest(newBookingRequest));
    }

    /**
     * @verifies throw BookingRequestNotFoundException when booking request is not found
     * @see BookingServiceImpl#acceptBooking(com.smartbyte.edubookschedulerbackend.business.request.AcceptBookingRequest)
     */
    @Test
    void acceptBooking_shouldThrowBookingRequestNotFoundExceptionWhenBookingRequestIsNotFound() {
        //Arrange

        AcceptBookingRequest request=AcceptBookingRequest.builder()
                .bookingRequestId(1L)
                .answer(0)
                .build();

        when(bookingRequestRepository.findById(1L)).thenReturn(Optional.empty());

        //Act + Assert
        assertThrows(BookingRequestNotFoundException.class,()->bookingService.acceptBooking(request));

    }

    /**
     * @verifies throw ResponseStatusException when new answer is invalid
     * @see BookingServiceImpl#acceptBooking(com.smartbyte.edubookschedulerbackend.business.request.AcceptBookingRequest)
     */
    @Test
    void acceptBooking_shouldThrowResponseStatusExceptionWhenNewAnswerIsInvalid() {
        //Arrange
        AcceptBookingRequest request=AcceptBookingRequest.builder()
                .bookingRequestId(1L)
                .answer(4)
                .build();

        BookingRequestEntity bookingRequestEntity=BookingRequestEntity.builder()
                .id(request.getBookingRequestId())
                .answer(BookingRequestAnswer.NoAnswer)
                .build();

        BookingRequest bookingRequest=BookingRequest.builder()
                .id(bookingRequestEntity.getId())
                .answer(bookingRequestEntity.getAnswer())
                .build();

        when(bookingRequestRepository.findById(1L)).thenReturn(Optional.of(bookingRequestEntity));

        when(converter.convertFromBookingRequestEntity(bookingRequestEntity)).thenReturn(bookingRequest);

        //Act + Assert
        assertThrows(ResponseStatusException.class,()->bookingService.acceptBooking(request));
    }

    /**
     * @verifies throw ResponseStatusException when booking request answer is not no answer
     * @see BookingServiceImpl#acceptBooking(com.smartbyte.edubookschedulerbackend.business.request.AcceptBookingRequest)
     */
    @Test
    void acceptBooking_shouldThrowResponseStatusExceptionWhenBookingRequestAnswerIsNotNoAnswer() {
        //Arrange
        AcceptBookingRequest request=AcceptBookingRequest.builder()
                .bookingRequestId(1L)
                .answer(0)
                .build();

        BookingRequestEntity bookingRequestEntity=BookingRequestEntity.builder()
                .id(request.getBookingRequestId())
                .answer(BookingRequestAnswer.Accepted)
                .build();

        BookingRequest bookingRequest=BookingRequest.builder()
                .id(bookingRequestEntity.getId())
                .answer(bookingRequestEntity.getAnswer())
                .build();

        when(bookingRequestRepository.findById(1L)).thenReturn(Optional.of(bookingRequestEntity));

        when(converter.convertFromBookingRequestEntity(bookingRequestEntity)).thenReturn(bookingRequest);

        //Act + Assert
        assertThrows(ResponseStatusException.class,()->bookingService.acceptBooking(request));
    }

    /**
     * @verifies throw ResponseStatusException when new answer is no answer
     * @see BookingServiceImpl#acceptBooking(com.smartbyte.edubookschedulerbackend.business.request.AcceptBookingRequest)
     */
    @Test
    void acceptBooking_shouldThrowResponseStatusExceptionWhenNewAnswerIsNoAnswer() {
        //Arrange
        AcceptBookingRequest request=AcceptBookingRequest.builder()
                .bookingRequestId(1L)
                .answer(0)
                .build();

        BookingRequestEntity bookingRequestEntity=BookingRequestEntity.builder()
                .id(request.getBookingRequestId())
                .answer(BookingRequestAnswer.NoAnswer)
                .build();

        BookingRequest bookingRequest=BookingRequest.builder()
                .id(bookingRequestEntity.getId())
                .answer(bookingRequestEntity.getAnswer())
                .build();

        when(bookingRequestRepository.findById(1L)).thenReturn(Optional.of(bookingRequestEntity));

        when(converter.convertFromBookingRequestEntity(bookingRequestEntity)).thenReturn(bookingRequest);

        //Act + Assert
        assertThrows(ResponseStatusException.class,()->bookingService.acceptBooking(request));
    }

    /**
     * @verifies throw ResponseStatusException when new answer is accepted, booking request's type is schedule, and booking state is not requested
     * @see BookingServiceImpl#acceptBooking(com.smartbyte.edubookschedulerbackend.business.request.AcceptBookingRequest)
     */
    @Test
    void acceptBooking_shouldThrowResponseStatusExceptionWhenNewAnswerIsAcceptedBookingRequestsTypeIsScheduleAndBookingStateIsNotRequested() {
        //Arrange
        AcceptBookingRequest request=AcceptBookingRequest.builder()
                .bookingRequestId(1L)
                .answer(1)
                .build();

        Booking booking=Booking.builder()
                .id(1L)
                .state(State.Scheduled)
                .build();

        BookingRequestEntity bookingRequestEntity=BookingRequestEntity.builder()
                .id(request.getBookingRequestId())
                .answer(BookingRequestAnswer.NoAnswer)
                .requestType(BookingRequestType.Schedule)
                .build();

        BookingRequest bookingRequest=BookingRequest.builder()
                .id(bookingRequestEntity.getId())
                .answer(bookingRequestEntity.getAnswer())
                .requestType(BookingRequestType.Schedule)
                .bookingToSchedule(booking)
                .build();



        when(bookingRequestRepository.findById(1L)).thenReturn(Optional.of(bookingRequestEntity));

        when(converter.convertFromBookingRequestEntity(bookingRequestEntity)).thenReturn(bookingRequest);

        //Act + Assert
        assertThrows(ResponseStatusException.class,()->bookingService.acceptBooking(request));

        verify(bookingRequestRepository).updateAnswer(
                bookingRequest.getId(),
                BookingRequestAnswer.values()[request.getAnswer()]
        );
    }

    /**
     * @verifies throw ResponseStatusException when new answer is accepted, booking request's type is reschedule, and booking state is not reschedule requested
     * @see BookingServiceImpl#acceptBooking(com.smartbyte.edubookschedulerbackend.business.request.AcceptBookingRequest)
     */
    @Test
    void acceptBooking_shouldThrowResponseStatusExceptionWhenNewAnswerIsAcceptedBookingRequestsTypeIsRescheduleAndBookingStateIsNotRescheduleRequested() {
        //Arrange
        AcceptBookingRequest request=AcceptBookingRequest.builder()
                .bookingRequestId(1L)
                .answer(1)
                .build();

        Booking rescheduledBooking=Booking.builder()
                .id(1L)
                .state(State.Scheduled)
                .build();

        BookingRequestEntity bookingRequestEntity=BookingRequestEntity.builder()
                .id(request.getBookingRequestId())
                .answer(BookingRequestAnswer.NoAnswer)
                .requestType(BookingRequestType.Reschedule)
                .build();

        BookingRequest bookingRequest=BookingRequest.builder()
                .id(bookingRequestEntity.getId())
                .answer(bookingRequestEntity.getAnswer())
                .requestType(BookingRequestType.Reschedule)
                .bookingToReschedule(rescheduledBooking)
                .build();



        when(bookingRequestRepository.findById(1L)).thenReturn(Optional.of(bookingRequestEntity));

        when(converter.convertFromBookingRequestEntity(bookingRequestEntity)).thenReturn(bookingRequest);

        //Act + Assert
        assertThrows(ResponseStatusException.class,()->bookingService.acceptBooking(request));

        verify(bookingRequestRepository).updateAnswer(
                bookingRequest.getId(),
                BookingRequestAnswer.values()[request.getAnswer()]
        );
    }

    /**
     * @verifies throw ResponseStatusException when new answer is accepted, booking request's type is reschedule, and rescheduled booking state is not reshedule wait accept
     * @see BookingServiceImpl#acceptBooking(com.smartbyte.edubookschedulerbackend.business.request.AcceptBookingRequest)
     */
    @Test
    void acceptBooking_shouldThrowResponseStatusExceptionWhenNewAnswerIsAcceptedBookingRequestsTypeIsRescheduleAndRescheduledBookingStateIsNotResheduleWaitAccept() {
        //Arrange
        AcceptBookingRequest request=AcceptBookingRequest.builder()
                .bookingRequestId(1L)
                .answer(1)
                .build();

        Booking rescheduledBooking=Booking.builder()
                .id(1L)
                .state(State.Reschedule_Requested)
                .build();

        Booking booking=Booking.builder()
                .id(1L)
                .state(State.Scheduled)
                .build();

        BookingRequestEntity bookingRequestEntity=BookingRequestEntity.builder()
                .id(request.getBookingRequestId())
                .answer(BookingRequestAnswer.NoAnswer)
                .requestType(BookingRequestType.Reschedule)
                .build();

        BookingRequest bookingRequest=BookingRequest.builder()
                .id(bookingRequestEntity.getId())
                .answer(bookingRequestEntity.getAnswer())
                .requestType(BookingRequestType.Reschedule)
                .bookingToReschedule(rescheduledBooking)
                .bookingToSchedule(booking)
                .build();

        when(bookingRequestRepository.findById(1L)).thenReturn(Optional.of(bookingRequestEntity));

        when(converter.convertFromBookingRequestEntity(bookingRequestEntity)).thenReturn(bookingRequest);

        //Act + Assert
        assertThrows(ResponseStatusException.class,()->bookingService.acceptBooking(request));

        verify(bookingRequestRepository).updateAnswer(
                bookingRequest.getId(),
                BookingRequestAnswer.values()[request.getAnswer()]
        );
    }

    /**
     * @verifies accept booking when new answer is accepted and booking request's type is schedule
     * @see BookingServiceImpl#acceptBooking(AcceptBookingRequest)
     */
    @Test
    void acceptBooking_shouldAcceptBookingWhenNewAnswerIsAcceptedAndBookingRequestsTypeIsSchedule() {
        //Arrange
        AcceptBookingRequest request=AcceptBookingRequest.builder()
                .bookingRequestId(1L)
                .answer(1)
                .build();

        Booking booking=Booking.builder()
                .id(1L)
                .state(State.Requested)
                .build();

        BookingEntity bookingEntity=BookingEntity.builder()
                .id(booking.getId())
                .state(State.Requested.getStateId())
                .build();

        BookingRequestEntity bookingRequestEntity=BookingRequestEntity.builder()
                .id(request.getBookingRequestId())
                .answer(BookingRequestAnswer.NoAnswer)
                .requestType(BookingRequestType.Schedule)
                .bookingToSchedule(bookingEntity)
                .build();

        BookingRequest bookingRequest=BookingRequest.builder()
                .id(bookingRequestEntity.getId())
                .answer(bookingRequestEntity.getAnswer())
                .requestType(BookingRequestType.Schedule)
                .bookingToSchedule(booking)
                .build();

        when(bookingRequestRepository.findById(1L)).thenReturn(Optional.of(bookingRequestEntity));

        when(converter.convertFromBookingRequestEntity(bookingRequestEntity)).thenReturn(bookingRequest);

        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(bookingEntity));

        when(converter.convertFromBookingEntity(bookingEntity)).thenReturn(booking);

        //Act

        bookingService.acceptBooking(request);

        //Assert

        verify(bookingRequestRepository).updateAnswer(
                bookingRequest.getId(),
                BookingRequestAnswer.values()[request.getAnswer()]
        );

        verify(bookingRepository).updateBookingState(booking.getId(),State.Scheduled.getStateId());

    }

    /**
     * @verifies accept booking when new answer is accepted and booking request's type is reschedule
     * @see BookingServiceImpl#acceptBooking(AcceptBookingRequest)
     */
    @Test
    void acceptBooking_shouldAcceptBookingWhenNewAnswerIsAcceptedAndBookingRequestsTypeIsReschedule() {
        //Arrange
        AcceptBookingRequest request=AcceptBookingRequest.builder()
                .bookingRequestId(1L)
                .answer(1)
                .build();

        Booking rescheduledBooking=Booking.builder()
                .id(1L)
                .state(State.Reschedule_Requested)
                .build();

        Booking booking=Booking.builder()
                .id(2L)
                .state(State.Reschedule_Wait_Accept)
                .build();

        BookingEntity rescheduledBookingEntity=BookingEntity.builder()
                .id(rescheduledBooking.getId())
                .state(rescheduledBooking.getState().getStateId())
                .build();

        BookingEntity bookingEntity=BookingEntity.builder()
                .id(booking.getId())
                .state(booking.getState().getStateId())
                .build();

        BookingRequestEntity bookingRequestEntity=BookingRequestEntity.builder()
                .id(request.getBookingRequestId())
                .answer(BookingRequestAnswer.NoAnswer)
                .requestType(BookingRequestType.Reschedule)
                .bookingToSchedule(bookingEntity)
                .bookingToReschedule(rescheduledBookingEntity)
                .build();

        BookingRequest bookingRequest=BookingRequest.builder()
                .id(bookingRequestEntity.getId())
                .answer(bookingRequestEntity.getAnswer())
                .requestType(BookingRequestType.Reschedule)
                .bookingToReschedule(rescheduledBooking)
                .bookingToSchedule(booking)
                .build();

        when(bookingRequestRepository.findById(1L)).thenReturn(Optional.of(bookingRequestEntity));

        when(converter.convertFromBookingRequestEntity(bookingRequestEntity)).thenReturn(bookingRequest);

        when(bookingRepository.findById(rescheduledBooking.getId())).thenReturn(Optional.of(rescheduledBookingEntity));

        when(converter.convertFromBookingEntity(rescheduledBookingEntity)).thenReturn(rescheduledBooking);

        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(bookingEntity));

        when(converter.convertFromBookingEntity(bookingEntity)).thenReturn(booking);

        //Act

        bookingService.acceptBooking(request);

        //Assert

        verify(bookingRequestRepository).updateAnswer(
                bookingRequest.getId(),
                BookingRequestAnswer.values()[request.getAnswer()]
        );

        verify(bookingRepository).updateBookingState(rescheduledBooking.getId(),State.Rescheduled.getStateId());
        verify(bookingRepository).updateBookingState(booking.getId(),State.Scheduled.getStateId());
    }

    /**
     * @verifies cancel booking when new answer is rejected
     * @see BookingServiceImpl#acceptBooking(AcceptBookingRequest)
     */
    @Test
    void acceptBooking_shouldCancelBookingWhenNewAnswerIsRejected() {
        //Arrange
        AcceptBookingRequest request=AcceptBookingRequest.builder()
                .bookingRequestId(1L)
                .answer(2)
                .build();

        Booking booking=Booking.builder()
                .id(2L)
                .state(State.Reschedule_Wait_Accept)
                .build();

        BookingEntity bookingEntity=BookingEntity.builder()
                .id(booking.getId())
                .state(booking.getState().getStateId())
                .build();

        BookingRequestEntity bookingRequestEntity=BookingRequestEntity.builder()
                .id(request.getBookingRequestId())
                .answer(BookingRequestAnswer.NoAnswer)
                .requestType(BookingRequestType.Reschedule)
                .bookingToSchedule(bookingEntity)
                .build();

        BookingRequest bookingRequest=BookingRequest.builder()
                .id(bookingRequestEntity.getId())
                .answer(bookingRequestEntity.getAnswer())
                .requestType(BookingRequestType.Reschedule)
                .bookingToSchedule(booking)
                .build();

        when(bookingRequestRepository.findById(1L)).thenReturn(Optional.of(bookingRequestEntity));

        when(converter.convertFromBookingRequestEntity(bookingRequestEntity)).thenReturn(bookingRequest);

        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(bookingEntity));

        when(converter.convertFromBookingEntity(bookingEntity)).thenReturn(booking);

        //Act

        bookingService.acceptBooking(request);

        //Assert

        verify(bookingRequestRepository).updateAnswer(
                bookingRequest.getId(),
                BookingRequestAnswer.values()[request.getAnswer()]
        );

        verify(bookingRepository).updateBookingState(booking.getId(),State.Cancelled.getStateId());
    }

    /**
     * @verifies cancel rescheduled booking when new answer is rejected and booking request has rescheduled a booking
     * @see BookingServiceImpl#acceptBooking(AcceptBookingRequest)
     */
    @Test
    void acceptBooking_shouldCancelRescheduledBookingWhenNewAnswerIsRejectedAndBookingRequestHasRescheduledABooking() {
        //Arrange
        AcceptBookingRequest request=AcceptBookingRequest.builder()
                .bookingRequestId(1L)
                .answer(2)
                .build();

        Booking rescheduledBooking=Booking.builder()
                .id(1L)
                .state(State.Reschedule_Requested)
                .build();

        Booking booking=Booking.builder()
                .id(2L)
                .state(State.Reschedule_Wait_Accept)
                .build();

        BookingEntity rescheduledBookingEntity=BookingEntity.builder()
                .id(rescheduledBooking.getId())
                .state(rescheduledBooking.getState().getStateId())
                .build();

        BookingEntity bookingEntity=BookingEntity.builder()
                .id(booking.getId())
                .state(booking.getState().getStateId())
                .build();

        BookingRequestEntity bookingRequestEntity=BookingRequestEntity.builder()
                .id(request.getBookingRequestId())
                .answer(BookingRequestAnswer.NoAnswer)
                .requestType(BookingRequestType.Reschedule)
                .bookingToSchedule(bookingEntity)
                .bookingToReschedule(rescheduledBookingEntity)
                .build();

        BookingRequest bookingRequest=BookingRequest.builder()
                .id(bookingRequestEntity.getId())
                .answer(bookingRequestEntity.getAnswer())
                .requestType(BookingRequestType.Reschedule)
                .bookingToReschedule(rescheduledBooking)
                .bookingToSchedule(booking)
                .build();

        when(bookingRequestRepository.findById(1L)).thenReturn(Optional.of(bookingRequestEntity));

        when(converter.convertFromBookingRequestEntity(bookingRequestEntity)).thenReturn(bookingRequest);

        when(bookingRepository.findById(rescheduledBooking.getId())).thenReturn(Optional.of(rescheduledBookingEntity));

        when(converter.convertFromBookingEntity(rescheduledBookingEntity)).thenReturn(rescheduledBooking);

        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(bookingEntity));

        when(converter.convertFromBookingEntity(bookingEntity)).thenReturn(booking);

        //Act

        bookingService.acceptBooking(request);

        //Assert

        verify(bookingRequestRepository).updateAnswer(
                bookingRequest.getId(),
                BookingRequestAnswer.values()[request.getAnswer()]
        );

        verify(bookingRepository).updateBookingState(rescheduledBooking.getId(),State.Cancelled.getStateId());
        verify(bookingRepository).updateBookingState(booking.getId(),State.Cancelled.getStateId());
    }
}
