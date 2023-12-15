package com.smartbyte.edubookschedulerbackend.controller;

import com.smartbyte.edubookschedulerbackend.business.BookingService;
import com.smartbyte.edubookschedulerbackend.business.EmailService;
import com.smartbyte.edubookschedulerbackend.business.UserService;
import com.smartbyte.edubookschedulerbackend.business.request.*;
import com.smartbyte.edubookschedulerbackend.business.response.*;
import com.smartbyte.edubookschedulerbackend.domain.*;
import com.smartbyte.edubookschedulerbackend.domain.BookingRequest;
import com.smartbyte.edubookschedulerbackend.persistence.BookingRepository;
import com.smartbyte.edubookschedulerbackend.persistence.jpa.entity.EntityConverter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

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

    @PostMapping("/schedule")
    ResponseEntity<Void> scheduleBooking(@RequestBody @Valid ScheduleBookingRequest request){
        bookingService.scheduleBooking(request);

        return ResponseEntity.noContent().build();
    }
    @PostMapping("/cancel")
    ResponseEntity<Void> cancelBooking(@RequestBody UpdateBookingStateRequest request){
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
    }
    @PostMapping("/accept")
    ResponseEntity<Void> acceptBooking(@RequestBody AcceptBookingRequest request){

        bookingService.acceptBooking(request);

        Booking booking = bookingService.getBookingRequestById(request.getBookingRequestId())
                .map(r -> r.getBookingToSchedule()).get();

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

    }
    @PostMapping("/finish")
    ResponseEntity<Void>finishBooking(@RequestBody UpdateBookingStateRequest request){
        bookingService.finishBooking(request);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/reschedule")
    ResponseEntity<Void> rescheduleBooking(@RequestBody RescheduleBookingRequest request) {
        bookingService.rescheduleBooking(request);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/request/sentby/{id}")
    ResponseEntity<List<BookingRequest>> getRequestsSentBy(@PathVariable("id") long userId){
        User user = userService.getUser(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return ResponseEntity.ok(bookingService.getSentBookingRequests(user));
    }

    @GetMapping("/request/receivedby/{id}")
    ResponseEntity<List<BookingRequest>> getRequestsReceivedBy(@PathVariable("id") long userId){
        User user = userService.getUser(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return ResponseEntity.ok(bookingService.getReceivedBookingRequests(user));
    }
}
