package com.smartbyte.edubookschedulerbackend.business.Impl;

import com.smartbyte.edubookschedulerbackend.business.AvailabilityService;
import com.smartbyte.edubookschedulerbackend.business.exception.TutorNotFoundException;
import com.smartbyte.edubookschedulerbackend.business.request.CreateSetAvailabilityRequest;
import com.smartbyte.edubookschedulerbackend.business.request.GetSetSetAvailabilityRequest;
import com.smartbyte.edubookschedulerbackend.business.response.*;
import com.smartbyte.edubookschedulerbackend.domain.AvailabilityDomain;
import com.smartbyte.edubookschedulerbackend.domain.Tutor;
import com.smartbyte.edubookschedulerbackend.persistence.AvailabilityRepository;
import com.smartbyte.edubookschedulerbackend.persistence.jpa.entity.AvailabilityEntity;
import com.smartbyte.edubookschedulerbackend.persistence.jpa.entity.EntityConverter;
import com.smartbyte.edubookschedulerbackend.business.exception.UserNotFoundException;
import com.smartbyte.edubookschedulerbackend.business.request.GetAvailabilityRequest;
import com.smartbyte.edubookschedulerbackend.domain.Booking;
import com.smartbyte.edubookschedulerbackend.domain.User;
import com.smartbyte.edubookschedulerbackend.persistence.BookingRepository;
import com.smartbyte.edubookschedulerbackend.persistence.UserRepository;
import com.smartbyte.edubookschedulerbackend.persistence.jpa.entity.BookingEntity;
import com.smartbyte.edubookschedulerbackend.persistence.jpa.entity.UserEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class AvailabilityServiceImpl implements AvailabilityService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final AvailabilityRepository availabilityRepository;
    private final EntityConverter entityConverter;
    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    public AvailabilityServiceImpl(BookingRepository bookingRepository, UserRepository userRepository, AvailabilityRepository availabilityRepository, EntityConverter entityConverter) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.availabilityRepository = availabilityRepository;
        this.entityConverter = entityConverter;
    }

    /**
     * @param request GetAvailability request
     * @return List of GetAvailability response
     * @should return an empty list if no teacher found
     * @should return list of response when teacher are found
     * @should return list of response with unavailable teacher when teacher has booking
     */
    @Override
    public List<GetAvailabilityResponse> findAvailableTeachersByDateAndTime(GetAvailabilityRequest request) {
        // Guys explanation here we take the list of bookings that are in the selected date,time
        Date date = convertStringToDate(request.getDate());
        List<BookingEntity> bookings = bookingRepository.findByDateAndStartTimeAndEndTime(date, request.getStartTime(), request.getEndTime());

        // We take the ids of teachers here
        List<UserEntity> teachers = bookings.stream()
                .map(BookingEntity::getTutor)
                .distinct()
                .toList();
        List<UserEntity> allTeacher = userRepository.findByRole(1);
        return allTeacher.stream().map(userEntity -> GetAvailabilityResponse.builder()
                .name(userEntity.getName())
                .isAvailable(!teachers.contains(userEntity))
                .build()).toList();
    }

    /**
     *
     * @return GetUsersResponse
     *
     * @should return empty list if tutor is not found
     * @should return list of tutors if tutors are found
     */
    @Override
    public GetUsersResponse GetTutors() {
        List<UserEntity> tutorEntity = userRepository.findByRole(1);
        List<User> tutors = tutorEntity.stream()
                .map(entityConverter::convertFromUserEntity)
                .collect(Collectors.toList());
        GetUsersResponse response = GetUsersResponse.builder()
                .users(tutors)
                .build();
        return response;
    }

    /**
     *
     * @param id tutor's id
     * @return GetTutorsNameResponse or null
     *
     * @should return null if user is not found
     * @should return tutor's name if user is found
     */
    @Override
    public GetTutorsNameResponse GetTutorsName(long id) {
        Optional<UserEntity> user = userRepository.getUserById(id);
        if (user.isPresent()) {
            String response = user.get().getName();
            return GetTutorsNameResponse.builder()
                    .name(response)
                    .build();
        }
        return null;
    }

    /**
     *
     * @param id User's id
     * @return GetAvailabilityTutorResponse
     *
     * @should throw UserNotFoundException when user is not found
     * @should return GetAvailabilityTutorResponse
     */
    @Override
    public GetAvailabilityTutorResponse getTutorsBooking(long id) {
        Optional<UserEntity> user = userRepository.findById(id);
        if (user.isPresent()) {
            User tutor = entityConverter.convertFromUserEntity(user.get());
            List<Booking> bookings = new ArrayList<>();
            for (BookingEntity entity : bookingRepository.findByTutor(entityConverter.convertFromUser(tutor))) {
                bookings.add(entityConverter.convertFromBookingEntity(entity));
            }
            return GetAvailabilityTutorResponse.builder().bookings(bookings).build();
        } else {
            throw new UserNotFoundException();
        }
    }

    /**
     *
     * @param requests List of CreateSetAvailabilityRequest
     * @return List of CreateSetAvailabilityResponse
     *
     * @should throw RunTimeException if tutor is not found
     * @should throw RunTimeException if time overlaps
     * @should add new availabilities
     */
    @Transactional
    @Override
    public List<CreateSetAvailabilityResponse> createAvailability(List<CreateSetAvailabilityRequest> requests) {
        List<AvailabilityDomain> domains = requests.stream()
                .map(request -> {
                    Optional<UserEntity> tutor = userRepository.getUserById(request.getTutorId());
                    Tutor domain = tutor.map(userEntity -> (Tutor) entityConverter.convertFromUserEntity(userEntity))
                            .orElseThrow(() -> new RuntimeException("Tutor not found with ID: " + request.getTutorId()));

                    return AvailabilityDomain.builder()
                            .date(request.getDate())
                            .startTime(request.getStartTime())
                            .endTime(request.getEndTime())
                            .tutor(domain)
                            .build();
                })
                .collect(Collectors.toList());

        List<AvailabilityEntity> newAvailabilityEntities = domains.stream()
                .map(entityConverter::convertFromAvailabilityDomain)
                .collect(Collectors.toList());

        List<AvailabilityEntity> savedAvailabilityEntities = new ArrayList<>();

        for (AvailabilityEntity newAvailability : newAvailabilityEntities) {
//            try {
                List<AvailabilityEntity> existingAvailability = availabilityRepository
                        .findAllByTutorAndDate(newAvailability.getTutor(), newAvailability.getDate());

                boolean conflict = existingAvailability.stream()
                        .anyMatch(existing -> doTimesOverlap(newAvailability, existing));

                if (conflict) {
                    throw new RuntimeException("Availability conflict for tutor with ID: " + newAvailability.getTutor().getId());
                }

                AvailabilityEntity savedAvailabilityEntity = availabilityRepository.save(entityManager.merge(newAvailability));
                savedAvailabilityEntities.add(savedAvailabilityEntity);
        }

        List<AvailabilityDomain> availabilityDomains = savedAvailabilityEntities.stream()
                .map(entityConverter::convertFromAvailabilityEntity)
                .collect(Collectors.toList());
        return availabilityDomains.stream()
                .map(availabilityDomain -> CreateSetAvailabilityResponse.builder()
                        .availabilityDomain(availabilityDomain)
                        .build())
                .collect(Collectors.toList());

    }

    /**
     *
     * @param id User id
     * @return List of user's availabilities
     *
     * @should throw RunTimeException if user is not found
     * @should return List of availabilities45
     */
    @Override
    public List<GetSetAvailabilityResponse> getAvailabilityOfTutorWeekly(long id) {
        Optional<UserEntity> tutorOptional = userRepository.getUserById(id);
        if (tutorOptional.isPresent()) {
            UserEntity tutor = tutorOptional.get();
            LocalDate currentDate = LocalDate.now();
            int currentYear = currentDate.getYear();
            int currentMonth = currentDate.getMonthValue();
            LocalDate firstDayOfMonth = LocalDate.of(currentYear, currentMonth, 1);
            LocalDate lastDayOfMonth = firstDayOfMonth.with(TemporalAdjusters.lastDayOfMonth());
            Date startDate = Date.from(firstDayOfMonth.atStartOfDay(ZoneId.systemDefault()).toInstant());
            Date endDate = Date.from(lastDayOfMonth.atStartOfDay(ZoneId.systemDefault()).toInstant());


            GetAvailabilityTutorResponse bookingsResponse = getTutorsBooking(id);
            List<Booking> bookings = bookingsResponse.getBookings();

            List<AvailabilityEntity> availabilityList = availabilityRepository.findAllByTutorAndDateBetween(tutor, startDate, endDate);

            List<AvailabilityDomain> availabilityDomains = availabilityList.stream()
                    .map(entityConverter::convertFromAvailabilityEntity)
                    .toList();

            List<AvailabilityDomain> nonConflictingAvailability = availabilityDomains.stream()
                    .filter(availabilityDomain -> !hasConflict(availabilityDomain, bookings))
                    .toList();


            return nonConflictingAvailability.stream()
                    .map(availabilityDomain -> GetSetAvailabilityResponse.builder()
                            .availabilityDomain(availabilityDomain)
                            .build())
                    .collect(Collectors.toList());
        } else {
            throw new RuntimeException("Tutor not found with ID: " + id);
        }
    }

    private boolean hasConflict(AvailabilityDomain availabilityDomain, List<Booking> bookings) {
        for (Booking booking : bookings) {
            if (isConflict(availabilityDomain, booking)) {
                return true;
            }
        }
        return false;
    }

    private boolean isConflict(AvailabilityDomain availabilityDomain, Booking booking) {
        Date bookingDate = booking.getDate();
        int bookingStartTime = booking.getStartTime();
        int bookingEndTime = booking.getEndTime();

        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();

        cal1.setTime(availabilityDomain.getDate());
        cal2.setTime(bookingDate);

        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR) &&
                availabilityDomain.getStartTime() < bookingEndTime &&
                availabilityDomain.getEndTime() > bookingStartTime;
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

    private boolean doTimesOverlap(AvailabilityEntity newAvailability, AvailabilityEntity existingAvailability) {
        return newAvailability.getEndTime() > existingAvailability.getStartTime()
                && newAvailability.getStartTime() < existingAvailability.getEndTime();
    }

/*    @Override
    public List<Appointment> createDoctorSchedule(String token, DayOfWeek startDay, DayOfWeek endDay, LocalTime startTime, LocalTime endTime) {
        AccessToken tokenClaims =  accessTokenDecoder.decode(token);

        Long doctorId = tokenClaims.getId();

        Doctor doctor = doctorManager.getDoctor(doctorId);

        LocalDateTime currentDateTime = LocalDateTime.now().truncatedTo(ChronoUnit.HOURS);

        YearMonth yearMonth = YearMonth.of(currentDateTime.getYear(),currentDateTime.getMonth());
        numberOfDaysInMonth = yearMonth.lengthOfMonth();

        LocalDateTime endDateTime = currentDateTime.plusDays(numberOfDaysInMonth);

        List<Appointment> appointments = new ArrayList<>();

        while (currentDateTime.isBefore(endDateTime)){
            DayOfWeek dayOfWeek = DayOfWeek.values()[currentDateTime.get(ChronoField.DAY_OF_WEEK) - 1];

            if(dayOfWeek.compareTo(startDay) >= 0 && dayOfWeek.compareTo(endDay) <= 0){
                if(currentDateTime.getHour() >= startTime.getHour() && currentDateTime.getHour() < endTime.getHour()){
                    LocalDateTime appStart = currentDateTime;
                    LocalDateTime appEnd = currentDateTime.plusMinutes(appointmentDurationInMinutes);
                    appointments.add(Appointment.builder()
                            .dateAndStart(appStart)
                            .dateAndEnd(appEnd)
                            .doctor(doctor).build());
                }
            }

            currentDateTime = currentDateTime.plusHours(1);
        }
        return appointments;
    }*/
}

