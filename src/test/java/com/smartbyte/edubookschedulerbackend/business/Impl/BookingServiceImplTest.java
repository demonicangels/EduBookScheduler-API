package com.smartbyte.edubookschedulerbackend.business.Impl;

import com.smartbyte.edubookschedulerbackend.business.EntityConverter;
import com.smartbyte.edubookschedulerbackend.business.exception.BookingNotFoundException;
import com.smartbyte.edubookschedulerbackend.business.exception.InvalidBookingStateException;
import com.smartbyte.edubookschedulerbackend.business.exception.InvalidNewBookingStateException;
import com.smartbyte.edubookschedulerbackend.business.exception.UserNotFoundException;
import com.smartbyte.edubookschedulerbackend.business.request.UpdateBookingStateRequest;
import com.smartbyte.edubookschedulerbackend.business.response.GetUpcomingBookingsResponse;
import com.smartbyte.edubookschedulerbackend.domain.Booking;
import com.smartbyte.edubookschedulerbackend.domain.State;
import com.smartbyte.edubookschedulerbackend.persistence.BookingRepository;
import com.smartbyte.edubookschedulerbackend.persistence.UserRepository;
import com.smartbyte.edubookschedulerbackend.persistence.jpa.entity.BookingEntity;
import com.smartbyte.edubookschedulerbackend.persistence.jpa.entity.UserEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
 class BookingServiceImplTest {
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserRepository userRepository;
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

        UserEntity tutor=UserEntity.builder()
                .id(2L)
                .role(1)
                .name("tutor")
                .build();
        LocalDate currentDate=LocalDate.now();

        List<BookingEntity>bookings=List.of(BookingEntity.builder()
                .id(1L)
                .tutor(tutor)
                .startTime(720)
                .description("meeting")
                .date(new Date())
                .build(),
                BookingEntity.builder()
                        .id(2L)
                        .tutor(tutor)
                        .startTime(750)
                        .description("meeting 2")
                        .date(new Date())
                        .build()
        );

        when(bookingRepository.getUpcomingBookings(
                Date.from(currentDate.atStartOfDay(ZoneId.systemDefault()).toInstant()), student)
        ).thenReturn(bookings);

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
     * @verifies throw BookingNotFoundException if booking is not found
     * @see BookingServiceImpl#updateBookingState(com.smartbyte.edubookschedulerbackend.business.request.UpdateBookingStateRequest)
     */
    @Test
    void updateBookingState_shouldThrowBookingNotFoundExceptionIfBookingIsNotFound() {
        //Arrange
        UpdateBookingStateRequest request=UpdateBookingStateRequest.builder()
                .bookingId(1L)
                .bookingState(0)
                .build();

        when(bookingRepository.findById(request.getBookingId())).thenReturn(Optional.empty());

        //Act + Assert
        assertThrows(BookingNotFoundException.class,()->bookingService.updateBookingState(request));

    }

    /**
     * @verifies throw InvalidBookingStateException if booking status is not found
     * @see BookingServiceImpl#updateBookingState(com.smartbyte.edubookschedulerbackend.business.request.UpdateBookingStateRequest)
     */
    @Test
    void updateBookingState_shouldThrowInvalidBookingStateExceptionIfBookingStatusIsNotFound() {
        //Arrange
        UpdateBookingStateRequest request=UpdateBookingStateRequest.builder()
                .bookingId(1L)
                .bookingState(-1)
                .build();

        BookingEntity bookingEntity=BookingEntity.builder()
                .id(1L)
                .build();

        when(bookingRepository.findById(request.getBookingId())).thenReturn(Optional.of(bookingEntity));

        //Act + Assert
        assertThrows(InvalidBookingStateException.class,()->bookingService.updateBookingState(request));

    }


    private static Stream<Arguments>provideArgumentsFor_updateBookingState_shouldThrowInvalidNewBookingStateExceptionIfTheStateFlowIsInvalid(){
        return Stream.of(
                Arguments.of(0,0),
                Arguments.of(0,3),
                Arguments.of(0,5),
                Arguments.of(0,6),

                Arguments.of(1,0),
                Arguments.of(1,1),
                Arguments.of(1,3),

                Arguments.of(2,0),
                Arguments.of(2,1),
                Arguments.of(2,2),
                Arguments.of(2,5),
                Arguments.of(2,6),

                Arguments.of(3,0),
                Arguments.of(3,1),
                Arguments.of(3,2),
                Arguments.of(3,3),
                Arguments.of(3,4),
                Arguments.of(3,5),
                Arguments.of(3,6),

                Arguments.of(4,0),
                Arguments.of(4,1),
                Arguments.of(4,2),
                Arguments.of(4,3),
                Arguments.of(4,4),
                Arguments.of(4,5),
                Arguments.of(4,6),

                Arguments.of(5,0),
                Arguments.of(5,1),
                Arguments.of(5,3),
                Arguments.of(5,4),
                Arguments.of(5,5),
                Arguments.of(5,6),

                Arguments.of(6,0),
                Arguments.of(6,1),
                Arguments.of(6,2),
                Arguments.of(6,3),
                Arguments.of(6,4),
                Arguments.of(6,5),
                Arguments.of(6,6)
                );
    }

    /**
     * @verifies throw InvalidNewBookingStateException if the state flow is invalid
     * @see BookingServiceImpl#updateBookingState(com.smartbyte.edubookschedulerbackend.business.request.UpdateBookingStateRequest)
     */
    @ParameterizedTest
    @MethodSource("provideArgumentsFor_updateBookingState_shouldThrowInvalidNewBookingStateExceptionIfTheStateFlowIsInvalid")
    void updateBookingState_shouldThrowInvalidNewBookingStateExceptionIfTheStateFlowIsInvalid(int oldState, int newState) {
        //Arrange
        UpdateBookingStateRequest request=UpdateBookingStateRequest.builder()
                .bookingId(1L)
                .bookingState(newState)
                .build();

        BookingEntity bookingEntity=BookingEntity.builder()
                .id(1L)
                .state(oldState)
                .build();

        Booking booking=Booking.builder()
                .id(bookingEntity.getId())
                .state(State.fromStateId(bookingEntity.getState()))
                .build();

        when(bookingRepository.findById(request.getBookingId())).thenReturn(Optional.of(bookingEntity));

        when(converter.convertFromBookingEntity(bookingEntity)).thenReturn(booking);

        //Act + Assert
        assertThrows(InvalidNewBookingStateException.class,()->bookingService.updateBookingState(request));

    }

    private static Stream<Arguments>provideArgumentsFor_updateBookingState_shouldUpdateTheBookingStateIfTheRequestIsValid(){
        return Stream.of(
                Arguments.of(0,1),
                Arguments.of(0,2),
                Arguments.of(0,4),

                Arguments.of(1,2),
                Arguments.of(1,4),
                Arguments.of(1,5),
                Arguments.of(1,6),

                Arguments.of(2,3),
                Arguments.of(2,4),

                Arguments.of(5,2)
        );
    }

    /**
     * @verifies update the booking state if the request is valid
     * @see BookingServiceImpl#updateBookingState(com.smartbyte.edubookschedulerbackend.business.request.UpdateBookingStateRequest)
     */
    @ParameterizedTest
    @MethodSource("provideArgumentsFor_updateBookingState_shouldUpdateTheBookingStateIfTheRequestIsValid")
    void updateBookingState_shouldUpdateTheBookingStateIfTheRequestIsValid(int oldState,int newState) {
        //Arrange
        UpdateBookingStateRequest request=UpdateBookingStateRequest.builder()
                .bookingId(1L)
                .bookingState(newState)
                .build();

        BookingEntity bookingEntity=BookingEntity.builder()
                .id(1L)
                .state(oldState)
                .build();

        Booking booking=Booking.builder()
                .id(bookingEntity.getId())
                .state(State.fromStateId(bookingEntity.getState()))
                .build();

        when(bookingRepository.findById(request.getBookingId())).thenReturn(Optional.of(bookingEntity));

        when(converter.convertFromBookingEntity(bookingEntity)).thenReturn(booking);

        //Act
        bookingService.updateBookingState(request);

        //Assert
        verify(bookingRepository).updateBookingState(request.getBookingId(),request.getBookingState());

    }
}
