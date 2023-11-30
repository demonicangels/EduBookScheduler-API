package com.smartbyte.edubookschedulerbackend.persistence.jpa.entity;

import com.smartbyte.edubookschedulerbackend.domain.*;
import org.springframework.stereotype.Service;

@Service
public class EntityConverter {

    public BookingEntity convertFromBooking(Booking booking){
         if(booking == null) return null;
         return BookingEntity.builder()
                 .id(booking.getId())
                 .date(booking.getDate())
                 .startTime(booking.getStartTime())
                 .endTime(booking.getEndTime())
                 .description(booking.getDescription())
                 .student(convertFromUser(booking.getStudent()))
                 .tutor(convertFromUser(booking.getTutor()))
                 .state(booking.getState().getStateId())
                 .build();
    }

    public Booking convertFromBookingEntity(BookingEntity bookingEntity){
        if(bookingEntity == null) return null;
        return Booking.builder()
                .id(bookingEntity.getId())
                .date(bookingEntity.getDate())
                .startTime(bookingEntity.getStartTime())
                .endTime(bookingEntity.getEndTime())
                .description(bookingEntity.getDescription())
                .student(convertFromUserEntity(bookingEntity.getStudent()))
                .tutor(convertFromUserEntity(bookingEntity.getTutor()))
                .state(State.fromStateId(bookingEntity.getState()))
                .build();
    }
    public UserEntity convertFromUser(User user){
        if(user == null) return null;
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
        if(userEntity == null) return null;
        return switch(userEntity.getRole()){
            case 0 -> convertFromStudentEntity((StudentInfoEntity) userEntity);
            case 1 -> convertFromTutorEntity((TutorInfoEntity) userEntity);
            default -> throw new IllegalArgumentException("Unsupported user type");
        };
    }

    public Student convertFromStudentEntity(StudentInfoEntity studentInfoEntity){
        if(studentInfoEntity == null) return null;
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
        if(tutorInfoEntity == null) return null;
        return Tutor.builder()
                .id(tutorInfoEntity.getId())
                .name(tutorInfoEntity.getName())
                .email(tutorInfoEntity.getEmail())
                .password(tutorInfoEntity.getPassword())
                .profilePicURL(tutorInfoEntity.getProfilePicURL())
                .build();
    }

    public BookingRequestEntity convertFromBookingRequest(BookingRequest sbr){
        if(sbr == null) return null;
        return BookingRequestEntity.builder()
                .id(sbr.getId())
                .requestType(sbr.getRequestType())
                .requester(convertFromUser(sbr.getRequester()))
                .receiver(convertFromUser(sbr.getReceiver()))
                .bookingToSchedule(convertFromBooking(sbr.getBookingToSchedule()))
                .bookingToReschedule(convertFromBooking(sbr.getBookingToReschedule()))
                .answer(sbr.getAnswer())
                .build();
    }

    public BookingRequest convertFromBookingRequestEntity(BookingRequestEntity sbre) {
        if(sbre == null) return null;
        return BookingRequest.builder()
                .id(sbre.getId())
                .requestType(sbre.getRequestType())
                .requester(convertFromUserEntity(sbre.getRequester()))
                .receiver(convertFromUserEntity(sbre.getReceiver()))
                .bookingToSchedule(convertFromBookingEntity(sbre.getBookingToSchedule()))
                .bookingToReschedule(convertFromBookingEntity(sbre.getBookingToReschedule()))
                .answer(sbre.getAnswer())
                .build();
    }
}
