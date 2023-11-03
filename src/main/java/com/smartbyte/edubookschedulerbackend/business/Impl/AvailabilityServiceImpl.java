package com.smartbyte.edubookschedulerbackend.business.Impl;

import com.smartbyte.edubookschedulerbackend.business.AvailabilityService;
import com.smartbyte.edubookschedulerbackend.business.request.GetAvailabilityRequest;
import com.smartbyte.edubookschedulerbackend.business.response.GetAvailabilityResponse;
import com.smartbyte.edubookschedulerbackend.persistence.BookingRepository;
import com.smartbyte.edubookschedulerbackend.persistence.UserRepository;
import com.smartbyte.edubookschedulerbackend.persistence.jpa.entity.BookingEntity;
import com.smartbyte.edubookschedulerbackend.persistence.jpa.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AvailabilityServiceImpl implements AvailabilityService {

    private BookingRepository bookingRepository;

    private UserRepository userRepository;

    @Override
    public List<GetAvailabilityResponse> findAvailableTeachersByDateAndTime(GetAvailabilityRequest request) {
       // Guys explanation here we take the list of bookings that are in the selected date,time
        List<BookingEntity> bookings = bookingRepository.findByDateAndStartTimeAndEndTime(request.getDate(), request.getStartTime(), request.getEndTime());

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
}

