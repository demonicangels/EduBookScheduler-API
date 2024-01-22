package com.smartbyte.edubookschedulerbackend.controller;

import com.smartbyte.edubookschedulerbackend.business.BookingService;
import com.smartbyte.edubookschedulerbackend.business.EmailService;
import com.smartbyte.edubookschedulerbackend.business.UserService;
import com.smartbyte.edubookschedulerbackend.business.request.*;
import com.smartbyte.edubookschedulerbackend.business.response.*;
import com.smartbyte.edubookschedulerbackend.business.security.token.AccessToken;
import com.smartbyte.edubookschedulerbackend.domain.*;
import com.smartbyte.edubookschedulerbackend.domain.BookingRequest;
import jakarta.annotation.security.RolesAllowed;
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
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

@RestController
@RequestMapping("/booking")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:4173"})
public class BookingController {

    private final BookingService bookingService;
    private final UserService userService;
    private final EmailService emailService;
    private final AccessToken accessToken;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;


    @RolesAllowed({"Student","Admin", "Tutor"})
    @GetMapping("/{id}/{token}")
    ResponseEntity<GetBookingByIdResponse> getBookingById(@PathVariable("id") long id) {


        boolean isStudent = accessToken.hasRole(Role.Student.name());
        boolean isAdmin = accessToken.hasRole(Role.Admin.name());
        boolean isTutor = accessToken.hasRole(Role.Tutor.name());
        Optional<User> user = userService.getUser(accessToken.getId());



        if(!user.isEmpty() && (isStudent || isAdmin || isTutor)){

                Optional<Booking> optBooking = bookingService.getBookingById(id);
                if (!optBooking.isEmpty()){
                    Booking booking = optBooking.get();
                    GetBookingByIdResponse response = GetBookingByIdResponse.builder()
                            .booking(booking)
                            .build();
                    return ResponseEntity.ok(response);
                }
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @RolesAllowed({"Student", "Tutor", "Admin"})
    @GetMapping("/user/{id}")
    ResponseEntity<GetUsersBookingResponse> getUsersBooking(@PathVariable("id") long id) {

        boolean isStudent = accessToken.hasRole(Role.Student.name());
        boolean isAdmin = accessToken.hasRole(Role.Admin.name());
        boolean isTutor = accessToken.hasRole(Role.Tutor.name());
        boolean isAuthorizedUser = accessToken.getId() == id;
        Optional<User> userFromDb = userService.getUser(id);
        List<Booking> wantedBookings = bookingService.getUsersBooking(userFromDb.get());


        if(!userFromDb.isEmpty() && (isStudent || isTutor || isAdmin) && !wantedBookings.isEmpty() && isAuthorizedUser){
            boolean containsUserId = wantedBookings
                    .stream()
                    .anyMatch(app -> app.getTutor().getId().equals(id) || app.getStudent().getId().equals(id));

            if(containsUserId){
                User user = userFromDb.get();
                List<Booking> bookings = bookingService.getUsersBooking(user);
                GetUsersBookingResponse response = GetUsersBookingResponse.builder()
                        .bookings(bookings)
                        .build();
                return ResponseEntity.ok(response);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @RolesAllowed({"Student", "Tutor", "Admin"})
    @GetMapping("upcoming/{studentId}")
    ResponseEntity<List<GetUpcomingBookingsResponse>>getUpcomingBookings
            (@PathVariable(value = "studentId")long studentId){

        boolean isStudent = accessToken.hasRole(Role.Student.name());
        boolean isAdmin = accessToken.hasRole(Role.Admin.name());
        boolean isTutor = accessToken.hasRole(Role.Tutor.name());
        boolean isAuthorizedUser = accessToken.getId() == studentId;
        Optional<User> userFromDb = userService.getUser(studentId);

        if(!userFromDb.isEmpty() && (isStudent || isAdmin || isTutor) && isAuthorizedUser){
            return ResponseEntity.ok(bookingService.getUpcomingBookings(studentId));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @RolesAllowed({"Student", "Tutor"})
    @PostMapping("/schedule")
    ResponseEntity<Void> scheduleBooking(@RequestBody @Valid ScheduleBookingRequest request){

        boolean isStudent = accessToken.hasRole(Role.Student.name());
        boolean isTutor = accessToken.hasRole(Role.Tutor.name());
        boolean isAuthorizedUser = accessToken.getId().equals(request.getRequesterId());
        Optional<User> userFromDb = userService.getUser(request.getRequesterId());


        if(!userFromDb.isEmpty() && (isStudent || isTutor) && isAuthorizedUser){
            bookingService.scheduleBooking(request);

            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

    }

    @RolesAllowed({"Student", "Tutor"})
    @PostMapping("/cancel")
    ResponseEntity<Void> cancelBooking(@RequestBody UpdateBookingStateRequest request){

        boolean isStudent = accessToken.hasRole(Role.Student.name());
        boolean isTutor = accessToken.hasRole(Role.Tutor.name());
        Optional<Booking> booking = bookingService.getBookingById(request.getBookingId());

        if(isStudent || isTutor && !booking.isEmpty()){
            Booking bookingToUpdate = booking.get();
            boolean containsUserId = Stream.of(bookingToUpdate.getStudent(), bookingToUpdate.getTutor())
                    .mapToLong(User::getId)
                    .anyMatch(userId -> Objects.equals(userId, accessToken.getId()));

            if(containsUserId){
                Date date = bookingToUpdate.getDate();
                LocalDateTime inputDateTime = LocalDateTime.parse(date.toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S"));
                DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("y-M-d");

                String tutor = bookingToUpdate.getTutor().getName();
                String about = bookingToUpdate.getDescription();

                SendEmailRequest emailRequest = SendEmailRequest.builder()
                        .message("Booking with " + tutor +
                                " on " + inputDateTime.format(outputFormatter) +
                                ". Description of the booking: " + about + "has been canceled. The tutor isn't available at this time. Please reschedule.")
                        .subject("Booking Canceled")
                        .build();
                emailService.sendEmail(emailRequest);
                bookingService.cancelBooking(request);
                return ResponseEntity.ok().build();
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @RolesAllowed({"Student", "Tutor"})
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

    @RolesAllowed({"Tutor"})
    @PostMapping("/finish")
    ResponseEntity<Void>finishBooking(@RequestBody UpdateBookingStateRequest request){
        boolean isTutor = accessToken.hasRole(Role.Tutor.name());
        Optional<Booking> booking = bookingService.getBookingById(request.getBookingId());

        if(isTutor && !booking.isEmpty()) {
            Booking bookingToUpdate = booking.get();
            boolean containsUserId = bookingToUpdate.getTutor().getId().equals(accessToken.getId());

            if (containsUserId) {
                bookingService.finishBooking(request);
                return ResponseEntity.ok().build();
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @RolesAllowed({"Student", "Tutor"})
    @PostMapping("/reschedule")
    ResponseEntity<Void> rescheduleBooking(@RequestBody RescheduleBookingRequest request) {

        boolean isStudent = accessToken.hasRole(Role.Student.name());
        boolean isTutor = accessToken.hasRole(Role.Tutor.name());
        boolean isAuthorizedUser = accessToken.getId().equals(request.getRequesterId());


        if((isStudent || isTutor) && isAuthorizedUser){

            bookingService.rescheduleBooking(request);

            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

    }

    @RolesAllowed({"Student", "Tutor"})
    @GetMapping("/request/sentby/{id}")
    ResponseEntity<List<BookingRequest>> getRequestsSentBy(@PathVariable("id") long userId){
        User user = userService.getUser(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return ResponseEntity.ok(bookingService.getSentBookingRequests(user));
    }

    @RolesAllowed({"Student", "Tutor"})
    @GetMapping("/request/receivedby/{id}")
    ResponseEntity<List<BookingRequest>> getRequestsReceivedBy(@PathVariable("id") long userId){
        User user = userService.getUser(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return ResponseEntity.ok(bookingService.getReceivedBookingRequests(user));
    }
}
