package com.smartbyte.edubookschedulerbackend.business.Impl;

import com.smartbyte.edubookschedulerbackend.business.exception.UserNotFoundException;
import com.smartbyte.edubookschedulerbackend.business.request.CreateSetAvailabilityRequest;
import com.smartbyte.edubookschedulerbackend.business.request.GetAvailabilityRequest;
import com.smartbyte.edubookschedulerbackend.business.response.*;
import com.smartbyte.edubookschedulerbackend.domain.*;
import com.smartbyte.edubookschedulerbackend.persistence.AvailabilityRepository;
import com.smartbyte.edubookschedulerbackend.persistence.BookingRepository;
import com.smartbyte.edubookschedulerbackend.persistence.UserRepository;
import com.smartbyte.edubookschedulerbackend.persistence.jpa.entity.AvailabilityEntity;
import com.smartbyte.edubookschedulerbackend.persistence.jpa.entity.BookingEntity;
import com.smartbyte.edubookschedulerbackend.persistence.jpa.entity.EntityConverter;
import com.smartbyte.edubookschedulerbackend.persistence.jpa.entity.UserEntity;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AvailabilityServiceImplTest {
    @Mock
    private BookingRepository bookingRepositoryMock;
    @Mock
    private UserRepository userRepositoryMock;
    @Mock
    private AvailabilityRepository availabilityRepositoryMock;

    @Mock
    private EntityConverter converter;
    @Mock
    private EntityManager entityManager;
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

    /**
     * @verifies return empty list if tutor is not found
     * @see AvailabilityServiceImpl#GetTutors()
     */
    @Test
    void GetTutors_shouldReturnEmptyListIfTutorIsNotFound() {
        //Arrange
        when(userRepositoryMock.findByRole(1)).thenReturn(List.of());

        GetUsersResponse expectedResponse=GetUsersResponse.builder()
                .users(List.of())
                .build();

        //Act
        GetUsersResponse actualResponse=availabilityService.GetTutors();

        //Assert
        assertEquals(expectedResponse,actualResponse);

    }

    /**
     * @verifies return list of tutors if tutors are found
     * @see AvailabilityServiceImpl#GetTutors()
     */
    @Test
    void GetTutors_shouldReturnListOfTutorsIfTutorsAreFound() {
        //Arrange
        List<UserEntity>tutorEntities=List.of(
                UserEntity.builder()
                        .id(1L)
                        .role(1)
                        .build(),
                UserEntity.builder()
                        .id(2L)
                        .role(1)
                        .build()
        );

        when(userRepositoryMock.findByRole(1)).thenReturn(tutorEntities);

        List<User>tutors=new ArrayList<>();

        for(UserEntity tutor:tutorEntities){
            Tutor newTutor=Tutor.builder()
                    .id(tutor.getId())
                    .role(Role.Tutor)
                    .build();
            when(converter.convertFromUserEntity(tutor)).thenReturn(newTutor);
            tutors.add(newTutor);
        }


        GetUsersResponse expectedResponse=GetUsersResponse.builder()
                .users(tutors)
                .build();

        //Act
        GetUsersResponse actualResponse=availabilityService.GetTutors();

        //Assert
        assertEquals(expectedResponse,actualResponse);
    }

    /**
     * @verifies return null if user is not found
     * @see AvailabilityServiceImpl#GetTutorsName(long)
     */
    @Test
    void GetTutorsName_shouldReturnNullIfUserIsNotFound() {
        //Arrange
        when(userRepositoryMock.getUserById(1L)).thenReturn(Optional.empty());

        //Act
        GetTutorsNameResponse response=availabilityService.GetTutorsName(1L);

        //Assert
        assertNull(response);

    }

    /**
     * @verifies return tutor's name if user is found
     * @see AvailabilityServiceImpl#GetTutorsName(long)
     */
    @Test
    void GetTutorsName_shouldReturnTutorsNameIfUserIsFound() {
        //Arrange
        UserEntity user=UserEntity.builder()
                .id(1L)
                .name("tutor")
                .build();

        when(userRepositoryMock.getUserById(1L)).thenReturn(Optional.of(user));

        GetTutorsNameResponse expectedResponse=GetTutorsNameResponse.builder()
                .name(user.getName())
                .build();

        //Act
        GetTutorsNameResponse actualResponse=availabilityService.GetTutorsName(1L);

        //Assert
        assertEquals(expectedResponse,actualResponse);

    }

    /**
     * @verifies throw RunTimeException if tutor is not found
     * @see AvailabilityServiceImpl#createAvailability(List)
     */
    @Test
    void createAvailability_shouldThrowRunTimeExceptionIfTutorIsNotFound() {
        //Arrange
        List<CreateSetAvailabilityRequest> requests=List.of(CreateSetAvailabilityRequest.builder()
                .tutorId(1L)
                .date(new Date())
                .startTime(1)
                .endTime(2)
                .build());

        for(CreateSetAvailabilityRequest request:requests){
            when(userRepositoryMock.getUserById(request.getTutorId())).thenReturn(Optional.empty());
        }

        //Act + Assert
        assertThrows(RuntimeException.class,()->availabilityService.createAvailability(requests));
    }

    /**
     * @verifies throw RunTimeException if time overlaps
     * @see AvailabilityServiceImpl#createAvailability(List)
     */
    @Test
    void createAvailability_shouldThrowRunTimeExceptionIfTimeOverlaps() {
        List<CreateSetAvailabilityRequest> requests=List.of(CreateSetAvailabilityRequest.builder()
                .tutorId(1L)
                .date(new Date())
                .startTime(1)
                .endTime(3)
                .build());

        UserEntity userEntity=UserEntity.builder()
                .id(1L)
                .role(1)
                .build();

        List<AvailabilityEntity>newAvailabilities=new ArrayList<>();

        for(CreateSetAvailabilityRequest request:requests){
            when(userRepositoryMock.getUserById(request.getTutorId())).thenReturn(Optional.of(userEntity));

            Tutor tutor=Tutor.builder()
                    .id(userEntity.getId())
                    .role(Role.Tutor)
                    .build();

            when(converter.convertFromUserEntity(userEntity)).thenReturn(tutor);

            AvailabilityDomain newAvailability=AvailabilityDomain.builder()
                    .date(request.getDate())
                    .startTime(request.getStartTime())
                    .endTime(request.getEndTime())
                    .tutor(tutor)
                    .build();

            AvailabilityEntity newAvailabilitiesEntity=AvailabilityEntity.builder()
                    .date(request.getDate())
                    .startTime(request.getStartTime())
                    .endTime(request.getEndTime())
                    .tutor(userEntity)
                    .build();

            when(converter.convertFromAvailabilityDomain(newAvailability)).thenReturn(newAvailabilitiesEntity);

            newAvailabilities.add(newAvailabilitiesEntity);
        }

        for (AvailabilityEntity newAvailability:newAvailabilities){
            List<AvailabilityEntity> existingAvailability=List.of(
                    AvailabilityEntity.builder()
                            .id(2L)
                            .startTime(0)
                            .endTime(2)
                            .tutor(newAvailability.getTutor())
                            .date(newAvailability.getDate())
                            .build()
            );

            when(availabilityRepositoryMock.findAllByTutorAndDate(newAvailability.getTutor(),
                    newAvailability.getDate()))
                    .thenReturn(existingAvailability);
        }

        //Act + Assert
        assertThrows(RuntimeException.class,()->availabilityService.createAvailability(requests));
    }

    /**
     * @verifies add new availabilities
     * @see AvailabilityServiceImpl#createAvailability(List)
     */
    @Test
    void createAvailability_shouldAddNewAvailabilities() {
        List<CreateSetAvailabilityRequest> requests=List.of(CreateSetAvailabilityRequest.builder()
                .tutorId(1L)
                .date(new Date())
                .startTime(1)
                .endTime(3)
                .build());

        UserEntity userEntity=UserEntity.builder()
                .id(1L)
                .role(1)
                .build();

        List<AvailabilityEntity>newAvailabilities=new ArrayList<>();

        for(CreateSetAvailabilityRequest request:requests){
            when(userRepositoryMock.getUserById(request.getTutorId())).thenReturn(Optional.of(userEntity));

            Tutor tutor=Tutor.builder()
                    .id(userEntity.getId())
                    .role(Role.Tutor)
                    .build();

            when(converter.convertFromUserEntity(userEntity)).thenReturn(tutor);

            AvailabilityDomain newAvailability=AvailabilityDomain.builder()
                    .date(request.getDate())
                    .startTime(request.getStartTime())
                    .endTime(request.getEndTime())
                    .tutor(tutor)
                    .build();

            AvailabilityEntity newAvailabilitiesEntity=AvailabilityEntity.builder()
                    .date(request.getDate())
                    .startTime(request.getStartTime())
                    .endTime(request.getEndTime())
                    .tutor(userEntity)
                    .build();

            when(converter.convertFromAvailabilityDomain(newAvailability)).thenReturn(newAvailabilitiesEntity);

            newAvailabilities.add(newAvailabilitiesEntity);
        }

        List<AvailabilityEntity>savedAvailabilityEntities=new ArrayList<>();

        for (AvailabilityEntity newAvailability:newAvailabilities){
            List<AvailabilityEntity> existingAvailability=List.of(
                    AvailabilityEntity.builder()
                            .id(1L)
                            .startTime(4)
                            .endTime(5)
                            .tutor(newAvailability.getTutor())
                            .date(newAvailability.getDate())
                            .build(),
                    AvailabilityEntity.builder()
                            .id(2L)
                            .startTime(-1)
                            .endTime(0)
                            .tutor(newAvailability.getTutor())
                            .date(newAvailability.getDate())
                            .build()
            );

            when(availabilityRepositoryMock.findAllByTutorAndDate(newAvailability.getTutor(),
                    newAvailability.getDate()))
                    .thenReturn(existingAvailability);

            newAvailability.setId(3L);

            when(entityManager.merge(newAvailability)).thenReturn(newAvailability);

            when(availabilityRepositoryMock.save(entityManager.merge(newAvailability))).thenReturn(newAvailability);

            savedAvailabilityEntities.add(newAvailability);
        }

        List<AvailabilityDomain>savedAvailabilities=new ArrayList<>();

        for (AvailabilityEntity savedAvailabilityEntity:savedAvailabilityEntities){

            Tutor tutor=Tutor.builder()
                    .id(savedAvailabilityEntity.getTutor().getId())
                    .role(Role.Tutor)
                    .build();

            AvailabilityDomain savedAvailability=AvailabilityDomain.builder()
                    .id(savedAvailabilityEntity.getId())
                    .tutor(tutor)
                    .endTime(savedAvailabilityEntity.getEndTime())
                    .date(savedAvailabilityEntity.getDate())
                    .startTime(savedAvailabilityEntity.getStartTime())
                    .build();

            when(converter.convertFromAvailabilityEntity(savedAvailabilityEntity)).thenReturn(savedAvailability);

            savedAvailabilities.add(savedAvailability);
        }

        List<CreateSetAvailabilityResponse> expectedResponses=savedAvailabilities.stream()
                .map(savedAvailability->CreateSetAvailabilityResponse.builder()
                        .availabilityDomain(savedAvailability)
                        .build()).toList();

        //Act
        List<CreateSetAvailabilityResponse>actualResponses=availabilityService.createAvailability(requests);

        //Assert
        assertEquals(expectedResponses,actualResponses);

    }

    /**
     * @verifies throw RunTimeException if user is not found
     * @see AvailabilityServiceImpl#getAvailabilityOfTutorWeekly(long)
     */
    @Test
    void getAvailabilityOfTutorWeekly_shouldThrowRunTimeExceptionIfUserIsNotFound() {
        //Arrange
        when(userRepositoryMock.getUserById(1L)).thenReturn(Optional.empty());

        //Act + Assert
        assertThrows(RuntimeException.class,()->availabilityService.getAvailabilityOfTutorWeekly(1L));

    }

    /**
     * @verifies return List of availabilities
     * @see AvailabilityServiceImpl#getAvailabilityOfTutorWeekly(long)
     */
    @Test
    void getAvailabilityOfTutorWeekly_shouldReturnListOfAvailabilities() {
        //Arrange
        UserEntity tutorEntity=UserEntity.builder()
                .id(1L)
                .role(Role.Tutor.getRoleId())
                .build();

        Tutor tutor=Tutor.builder()
                .id(1L)
                .role(Role.Tutor)
                .build();

        when(userRepositoryMock.getUserById(tutor.getId())).thenReturn(Optional.of(tutorEntity));
        when(userRepositoryMock.findById(tutor.getId())).thenReturn(Optional.of(tutorEntity));


        LocalDate currentDate = LocalDate.now();
        int currentYear = currentDate.getYear();
        int currentMonth = currentDate.getMonthValue();
        LocalDate firstDayOfMonth = LocalDate.of(currentYear, currentMonth, 1);
        LocalDate lastDayOfMonth = firstDayOfMonth.with(TemporalAdjusters.lastDayOfMonth());
        Date startDate = Date.from(firstDayOfMonth.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(lastDayOfMonth.atStartOfDay(ZoneId.systemDefault()).toInstant());

        List<BookingEntity>bookingEntities=List.of(
                BookingEntity.builder()
                        .id(1L)
                        .tutor(tutorEntity)
                        .build()
        );

        when(converter.convertFromUserEntity(tutorEntity)).thenReturn(tutor);

        when(converter.convertFromUser(tutor)).thenReturn(tutorEntity);

        when(bookingRepositoryMock.findByTutor(converter.convertFromUser(tutor))).thenReturn(bookingEntities);


        for (BookingEntity bookingEntity:bookingEntities){
            Booking booking=Booking.builder()
                    .id(bookingEntity.getId())
                    .tutor(tutor)
                    .date(startDate)
                    .startTime(3)
                    .endTime(4)
                    .build();

            when(converter.convertFromBookingEntity(bookingEntity)).thenReturn(booking);


        }

        List<AvailabilityEntity>availabilityEntities=List.of(
                AvailabilityEntity.builder()
                        .id(1L)
                        .tutor(tutorEntity)
                        .startTime(1)
                        .endTime(2)
                        .date(startDate)
                        .build()
        );

        when(availabilityRepositoryMock.findAllByTutorAndDateBetween(tutorEntity,startDate,endDate))
                .thenReturn(availabilityEntities);

        List<AvailabilityDomain> availabilities=new ArrayList<>();

        for (AvailabilityEntity availabilityEntity:availabilityEntities){
            AvailabilityDomain availability=AvailabilityDomain.builder()
                    .id(availabilityEntity.getId())
                    .tutor(tutor)
                    .endTime(availabilityEntity.getEndTime())
                    .startTime(availabilityEntity.getStartTime())
                    .date(availabilityEntity.getDate())
                    .build();

            when(converter.convertFromAvailabilityEntity(availabilityEntity)).thenReturn(availability);

            availabilities.add(availability);

        }

        List<GetSetAvailabilityResponse>expectedResponses=availabilities.stream()
                .map(availability->GetSetAvailabilityResponse.builder()
                        .availabilityDomain(availability)
                        .build()).toList();

        //Act
        List<GetSetAvailabilityResponse> actualResponses=availabilityService.getAvailabilityOfTutorWeekly(tutorEntity.getId());

        //Assert
        assertEquals(expectedResponses,actualResponses);

    }

    /**
     * @verifies throw UserNotFoundException when user is not found
     * @see AvailabilityServiceImpl#getTutorsBooking(long)
     */
    @Test
    void getTutorsBooking_shouldThrowUserNotFoundExceptionWhenUserIsNotFound() {
        //Arrange
        when(userRepositoryMock.findById(1L)).thenReturn(Optional.empty());

        //Act + Assert
        assertThrows(UserNotFoundException.class,()->availabilityService.getTutorsBooking(1L));

    }

    /**
     * @verifies return GetAvailabilityTutorResponse
     * @see AvailabilityServiceImpl#getTutorsBooking(long)
     */
    @Test
    void getTutorsBooking_shouldReturnGetAvailabilityTutorResponse() {
        //Arrange
        UserEntity tutorEntity=UserEntity.builder()
                .id(1L)
                .role(Role.Tutor.getRoleId())
                .build();

        when(userRepositoryMock.findById(tutorEntity.getId())).thenReturn(Optional.of(tutorEntity));

        Tutor tutor=Tutor.builder()
                .id(tutorEntity.getId())
                .role(Role.Tutor)
                .build();

        when(converter.convertFromUserEntity(tutorEntity)).thenReturn(tutor);

        List<BookingEntity>bookingEntities=List.of(
                BookingEntity.builder()
                        .id(1L)
                        .build()
        );

        when(bookingRepositoryMock.findByTutor(converter.convertFromUser(tutor))).thenReturn(bookingEntities);

        List<Booking>bookings=new ArrayList<>();

        for (BookingEntity bookingEntity:bookingEntities){
            Booking booking=Booking.builder()
                    .id(bookingEntity.getId())
                    .build();

            when(converter.convertFromBookingEntity(bookingEntity)).thenReturn(booking);

            bookings.add(booking);

        }

        GetAvailabilityTutorResponse expectedResponse=GetAvailabilityTutorResponse.builder()
                .bookings(bookings)
                .build();

        //Act

        GetAvailabilityTutorResponse actualResponse=availabilityService.getTutorsBooking(tutorEntity.getId());

        //Assert

        assertEquals(expectedResponse,actualResponse);

    }
}
