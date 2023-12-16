package com.smartbyte.edubookschedulerbackend.business.Impl;

import com.smartbyte.edubookschedulerbackend.domain.*;
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

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
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
}
