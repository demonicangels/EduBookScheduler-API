package com.smartbyte.edubookschedulerbackend.business.Impl;

import com.smartbyte.edubookschedulerbackend.business.request.GetAvailabilityRequest;
import com.smartbyte.edubookschedulerbackend.business.response.GetAvailabilityResponse;
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
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AvailabilityServiceImplTest {
    @Mock
    private BookingRepository bookingRepositoryMock;
    @Mock
    private UserRepository userRepositoryMock;
    @InjectMocks
    private AvailabilityServiceImpl availabilityService;
    /**
     * @verifies return an empty list if no teacher found
     * @see AvailabilityServiceImpl#findAvailableTeachersByDateAndTime(com.smartbyte.edubookschedulerbackend.business.request.GetAvailabilityRequest)
     */
    @Test
    void findAvailableTeachersByDateAndTime_shouldReturnAnEmptyListIfNoTeacherFound() throws Exception{
        //Arrange
        GetAvailabilityRequest request=GetAvailabilityRequest.builder()
                .date(LocalDate.now().toString())
                .startTime(7)
                .endTime(9)
                .build();

        SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
        Date date=dateFormat.parse(request.getDate());

        when(bookingRepositoryMock.findByDateAndStartTimeAndEndTime(date,request.getStartTime(),request.getEndTime()))
                .thenReturn(List.of());

        when(userRepositoryMock.findByRole(1)).thenReturn(List.of());

        //Act
        List<GetAvailabilityResponse>responses=availabilityService.findAvailableTeachersByDateAndTime(request);

        //Assert
        assertTrue(responses.isEmpty());

    }

    /**
     * @verifies return list of response when teacher are found
     * @see AvailabilityServiceImpl#findAvailableTeachersByDateAndTime(com.smartbyte.edubookschedulerbackend.business.request.GetAvailabilityRequest)
     */
    @Test
    void findAvailableTeachersByDateAndTime_shouldReturnListOfResponseWhenTeacherAreFound() throws Exception {
        //Arrange
        GetAvailabilityRequest request=GetAvailabilityRequest.builder()
                .date(LocalDate.now().toString())
                .startTime(7)
                .endTime(9)
                .build();

        List<UserEntity>teachers=List.of(
                UserEntity.builder()
                    .name("teacher1")
                    .id(2L)
                    .email("teacher1@fontys.nl")
                    .role(1)
                    .password("teacher1")
                    .build(),

                UserEntity.builder()
                        .name("teacher2")
                        .id(3L)
                        .email("teacher2@fontys.nl")
                        .role(1)
                        .password("teacher2")
                        .build()
        );

        SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
        Date date=dateFormat.parse(request.getDate());

        when(bookingRepositoryMock.findByDateAndStartTimeAndEndTime(date,request.getStartTime(),request.getEndTime()))
                .thenReturn(List.of());

        when(userRepositoryMock.findByRole(1)).thenReturn(teachers);

        List<GetAvailabilityResponse>expectedResponses=List.of(
                GetAvailabilityResponse.builder()
                        .isAvailable(true)
                        .name("teacher1")
                        .build(),

                GetAvailabilityResponse.builder()
                        .isAvailable(true)
                        .name("teacher2")
                        .build()
        );

        //Act
        List<GetAvailabilityResponse>actualResponses=availabilityService.findAvailableTeachersByDateAndTime(request);

        //Assert
        assertEquals(expectedResponses,actualResponses);
    }

    /**
     * @verifies return list of response with unavailable teacher when teacher has booking
     * @see AvailabilityServiceImpl#findAvailableTeachersByDateAndTime(GetAvailabilityRequest)
     */
    @Test
    public void findAvailableTeachersByDateAndTime_shouldReturnListOfResponseWithUnavailableTeacherWhenTeacherHasBooking() throws Exception {
        //Arrange
        GetAvailabilityRequest request=GetAvailabilityRequest.builder()
                .date(LocalDate.now().toString())
                .startTime(7)
                .endTime(9)
                .build();

        UserEntity student=UserEntity.builder()
                .id(4L)
                .email("student@fontys.nl")
                .password("student")
                .name("student")
                .role(0)
                .build();

        List<UserEntity>teachers=List.of(
                UserEntity.builder()
                        .name("teacher1")
                        .id(2L)
                        .email("teacher1@fontys.nl")
                        .role(1)
                        .password("teacher1")
                        .build(),

                UserEntity.builder()
                        .name("teacher2")
                        .id(3L)
                        .email("teacher2@fontys.nl")
                        .role(1)
                        .password("teacher2")
                        .build()
        );

        List<BookingEntity>bookings=List.of(BookingEntity.builder()
                        .date(new Date())
                        .startTime(6)
                        .endTime(8)
                        .description("Project review")
                        .id(1L)
                        .student(student)
                        .tutor(teachers.get(0))
                        .build());

        SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
        Date date=dateFormat.parse(request.getDate());

        when(bookingRepositoryMock.findByDateAndStartTimeAndEndTime(date,request.getStartTime(),request.getEndTime()))
                .thenReturn(bookings);

        when(userRepositoryMock.findByRole(1)).thenReturn(teachers);

        List<GetAvailabilityResponse>expectedResponses=List.of(
                GetAvailabilityResponse.builder()
                        .isAvailable(false)
                        .name("teacher1")
                        .build(),

                GetAvailabilityResponse.builder()
                        .isAvailable(true)
                        .name("teacher2")
                        .build()
        );

        //Act
        List<GetAvailabilityResponse>actualResponses=availabilityService.findAvailableTeachersByDateAndTime(request);

        //Assert
        assertEquals(expectedResponses,actualResponses);
    }
}
