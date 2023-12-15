package com.smartbyte.edubookschedulerbackend.controller;

import com.smartbyte.edubookschedulerbackend.business.BookingService;
import com.smartbyte.edubookschedulerbackend.business.EmailService;
import com.smartbyte.edubookschedulerbackend.business.UserService;
import com.smartbyte.edubookschedulerbackend.business.request.CreateBookingRequest;
import com.smartbyte.edubookschedulerbackend.business.request.RescheduleBookingRequest;
import com.smartbyte.edubookschedulerbackend.business.request.SendEmailRequest;
import com.smartbyte.edubookschedulerbackend.business.request.UpdateBookingStateRequest;
import com.smartbyte.edubookschedulerbackend.business.response.*;
import com.smartbyte.edubookschedulerbackend.domain.*;
import com.smartbyte.edubookschedulerbackend.persistence.BookingRepository;
import com.smartbyte.edubookschedulerbackend.persistence.jpa.entity.EntityConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/booking")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:4173"})
public class BookingController {
    private final BookingService bookingService;
    private final UserService userService;
    private final EmailService emailService;
    private final EntityConverter converter;
    private final BookingRepository bookingRepository;

    @GetMapping("/{id}")
    ResponseEntity<GetBookingByIdResponse> getBookingById(@PathVariable("id") long id) {
        Optional<Booking> optBooking = bookingService.getBookingById(id);
        if (optBooking.isEmpty())
            return ResponseEntity.notFound().build();
        Booking booking = optBooking.get();
        GetBookingByIdResponse response = GetBookingByIdResponse.builder()
                .booking(booking)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{id}")
    ResponseEntity<GetUsersBookingResponse> getUsersBooking(@PathVariable("id") long id) {
        Optional<User> optUser = userService.getUser(id);
        if (optUser.isEmpty())
            return ResponseEntity.notFound().build();

        User user = optUser.get();
        List<Booking> bookings = bookingService.getUsersBooking(user);
        GetUsersBookingResponse response = GetUsersBookingResponse.builder()
                .bookings(bookings)
                .build();
        return ResponseEntity.ok(response);

    }

    @GetMapping("upcoming/{studentId}")
    ResponseEntity<List<GetUpcomingBookingsResponse>>getUpcomingBookings
            (@PathVariable(value = "studentId")long studentId){
        return ResponseEntity.ok(bookingService.getUpcomingBookings(studentId));
    }

    @PostMapping("/add")
    ResponseEntity<CreateBookingResponse> createBooking(@RequestBody CreateBookingRequest request) {
        Optional<User> optStudent = userService.getUser(request.getStudentId());
        if (optStudent.isEmpty() || optStudent.get().getRole() != Role.Student)
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();
        Optional<User> optTutor = userService.getTutorByName(request.getTutorName());
        if (optTutor.isEmpty() || optTutor.get().getRole() != Role.Tutor)
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();

        User student = optStudent.get();
        User tutor = optTutor.get();

        Booking newBooking = (Booking.builder()
                .description(request.getDescription())
                .tutor(tutor)
                .student(student)
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .build());

        Booking new2Booking = bookingService.createBooking2(newBooking, request.getDate());


        CreateBookingResponse response = CreateBookingResponse.builder()
                .booking(new2Booking)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/schedule")
    ResponseEntity<Void>scheduleBooking(@RequestBody UpdateBookingStateRequest request){
        bookingService.scheduleBooking(request);
        return ResponseEntity.noContent().build();
    }
    @PutMapping("/cancel")
    ResponseEntity<Void>cancelBooking(@RequestBody UpdateBookingStateRequest request){
        Booking booking = bookingService.getBookingById(request.getBookingId()).get();

        Date date = booking.getDate();
        LocalDateTime inputDateTime = LocalDateTime.parse(date.toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S"));
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("y-M-d");

        String tutor = booking.getTutor().getName();
        String about = booking.getDescription();


        SendEmailRequest emailRequest = SendEmailRequest.builder()
                .message("Booking with " + tutor +
                        " on " + inputDateTime.format(outputFormatter) +
                        ". Description of the booking: " + about + "has been canceled. The tutor isn't available at this time. Please reschedule.")
                .subject("Booking Canceled")
                .build();
        emailService.sendEmail(emailRequest);
        bookingService.cancelBooking(request);

        return ResponseEntity.noContent().build();
    }@PutMapping("/accept")
    ResponseEntity<Void>acceptBooking(@RequestBody UpdateBookingStateRequest request){

        bookingService.acceptBooking(request);

        Booking booking = converter.convertFromBookingEntity(bookingRepository.findById(request.getBookingId()).get());

        Date date = booking.getDate();
        LocalDateTime inputDateTime = LocalDateTime.parse(date.toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S"));
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("y-M-d");

        String tutor = booking.getTutor().getName();
        String about = booking.getDescription();


        SendEmailRequest emailRequest = SendEmailRequest.builder()
                .message("Confirmation for booking with " + tutor +
                        " on " + inputDateTime.format(outputFormatter) +
                        ". Description of the booking: " + about)
                .subject("Booking Confirmation")
                .build();

        emailService.sendEmail(emailRequest);

        return ResponseEntity.noContent().build();

    }@PutMapping("/finish")
    ResponseEntity<Void>finishBooking(@RequestBody UpdateBookingStateRequest request){
        bookingService.finishBooking(request);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("")
    ResponseEntity<RescheduleBookingResponse> rescheduleBooking(@RequestBody RescheduleBookingRequest request) {
        Optional<User> optStudent = userService.getUser(request.getStudentId());
        if (optStudent.isEmpty() || optStudent.get().getRole() != Role.Student)
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();
        Optional<User> optTutor = userService.getUser(request.getTutorId());
        if (optTutor.isEmpty() || optTutor.get().getRole() != Role.Tutor)
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();

        Optional<Booking> optNewBooking = bookingService.rescheduleBooking(Booking.builder()
                .id(request.getId())
                .date(request.getDate())
                .description(request.getDescription())
//                        .student((Student)optStudent.get())
//                        .tutor((Tutor)optTutor.get())
                .build());
        if (optNewBooking.isEmpty())
            return ResponseEntity.unprocessableEntity().build();

        Booking newBooking = optNewBooking.get();
        RescheduleBookingResponse response = RescheduleBookingResponse.builder()
                .booking(newBooking)
                .build();
        return ResponseEntity.ok(response);
    }
}
