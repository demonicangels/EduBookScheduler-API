package com.smartbyte.edubookschedulerbackend.business.Impl;

import com.smartbyte.edubookschedulerbackend.business.AvailabilityService;
import com.smartbyte.edubookschedulerbackend.persistence.jpa.entity.EntityConverter;
import com.smartbyte.edubookschedulerbackend.business.exception.UserNotFoundException;
import com.smartbyte.edubookschedulerbackend.business.request.GetAvailabilityRequest;
import com.smartbyte.edubookschedulerbackend.business.response.GetAvailabilityResponse;
import com.smartbyte.edubookschedulerbackend.business.response.GetAvailabilityTutorResponse;
import com.smartbyte.edubookschedulerbackend.business.response.GetUsersResponse;
import com.smartbyte.edubookschedulerbackend.domain.Booking;
import com.smartbyte.edubookschedulerbackend.domain.User;
import com.smartbyte.edubookschedulerbackend.persistence.BookingRepository;
import com.smartbyte.edubookschedulerbackend.persistence.UserRepository;
import com.smartbyte.edubookschedulerbackend.persistence.jpa.entity.BookingEntity;
import com.smartbyte.edubookschedulerbackend.persistence.jpa.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AvailabilityServiceImpl implements AvailabilityService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private EntityConverter entityConverter;

    @Autowired
    public AvailabilityServiceImpl(BookingRepository bookingRepository, UserRepository userRepository, EntityConverter entityConverter) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
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

    private Date convertStringToDate(String dateString) {
        try {
            // Use SimpleDateFormat to parse the string to a Date
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
            return inputFormat.parse(dateString);
        } catch (ParseException e) {
            // Handle the exception or log it
            e.printStackTrace();
            return null;
        }
    }


}

