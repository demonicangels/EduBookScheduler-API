package com.smartbyte.edubookschedulerbackend.business;

import com.smartbyte.edubookschedulerbackend.domain.*;
import com.smartbyte.edubookschedulerbackend.persistence.jpa.entity.BookingEntity;
import com.smartbyte.edubookschedulerbackend.persistence.jpa.entity.StudentInfoEntity;
import com.smartbyte.edubookschedulerbackend.persistence.jpa.entity.TutorInfoEntity;
import com.smartbyte.edubookschedulerbackend.persistence.jpa.entity.UserEntity;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

@Service
public class EntityConverter {
    public BookingEntity convertFromBooking(Booking booking){

         return BookingEntity.builder()
                 .date(booking.getDate())
                 .startTime(booking.getStartTime())
                 .endTime(booking.getEndTime())
                 .description(booking.getDescription())
                 .student(convertFromUser(booking.getStudent()))
                 .tutor(convertFromUser(booking.getTutor()))
                 .build();
    }

    public Booking convertFromBookingEntity(BookingEntity bookingEntity){

        return Booking.builder()
                .date(bookingEntity.getDate())
                .startTime(bookingEntity.getStartTime())
                .endTime(bookingEntity.getEndTime())
                .description(bookingEntity.getDescription())
                .student(convertFromUserEntity(bookingEntity.getStudent()))
                .tutor(convertFromUserEntity(bookingEntity.getTutor()))
                .build();
    }
    public UserEntity convertFromUser(User user){

        return UserEntity.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .password(user.getPassword())
                .profilePicURL(user.getProfilePicURL())
                .role(user.getRole().ordinal())
                .build();
    }

    public User convertFromUserEntity(UserEntity userEntity){

        return switch(userEntity.getRole()){
            case 0 -> convertFromStudentEntity((StudentInfoEntity) userEntity);
            case 1 -> convertFromTutorEntity((TutorInfoEntity) userEntity);
            default -> throw new IllegalArgumentException("Unsupported user type");
        };
    }

    public Student convertFromStudentEntity(StudentInfoEntity studentInfoEntity){

        return Student.builder()
                .id(studentInfoEntity.getId())
                .name(studentInfoEntity.getName())
                .email(studentInfoEntity.getEmail())
                .password(studentInfoEntity.getPassword())
                .profilePicURL(studentInfoEntity.getProfilePicURL())
                .PCN(studentInfoEntity.getPcn())
                .build();
    }
    public Tutor convertFromTutorEntity(TutorInfoEntity tutorInfoEntity){

        return Tutor.builder()
                .id(tutorInfoEntity.getId())
                .name(tutorInfoEntity.getName())
                .email(tutorInfoEntity.getEmail())
                .password(tutorInfoEntity.getPassword())
                .profilePicURL(tutorInfoEntity.getProfilePicURL())
                .build();
    }
}
