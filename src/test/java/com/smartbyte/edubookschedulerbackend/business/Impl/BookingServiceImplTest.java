package com.smartbyte.edubookschedulerbackend.business.Impl;

import com.smartbyte.edubookschedulerbackend.business.EntityConverter;
import com.smartbyte.edubookschedulerbackend.business.exception.UserNotFoundException;
import com.smartbyte.edubookschedulerbackend.business.response.GetUpcomingBookingsResponse;
import com.smartbyte.edubookschedulerbackend.persistence.BookingRepository;
import com.smartbyte.edubookschedulerbackend.persistence.UserRepository;
import com.smartbyte.edubookschedulerbackend.persistence.jpa.entity.BookingEntity;
import com.smartbyte.edubookschedulerbackend.persistence.jpa.entity.UserEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
}
