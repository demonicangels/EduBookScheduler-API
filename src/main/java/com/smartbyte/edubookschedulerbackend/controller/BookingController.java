package com.smartbyte.edubookschedulerbackend.controller;

import com.smartbyte.edubookschedulerbackend.business.BookingService;
import com.smartbyte.edubookschedulerbackend.business.UserService;
import com.smartbyte.edubookschedulerbackend.business.request.CreateBookingRequest;
import com.smartbyte.edubookschedulerbackend.business.request.RescheduleBookingRequest;
import com.smartbyte.edubookschedulerbackend.business.response.CreateBookingResponse;
import com.smartbyte.edubookschedulerbackend.business.response.GetBookingByIdResponse;
import com.smartbyte.edubookschedulerbackend.business.response.GetUsersBookingResponse;
import com.smartbyte.edubookschedulerbackend.business.response.RescheduleBookingResponse;
import com.smartbyte.edubookschedulerbackend.domain.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/booking")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;
    private final UserService userService;

    @GetMapping("/{id}")
    ResponseEntity<GetBookingByIdResponse> getBookingById(@PathVariable("id") long id){
        Optional<Booking> optBooking = bookingService.getBookingById(id);
        if(optBooking.isEmpty())
            return ResponseEntity.notFound().build();
        Booking booking = optBooking.get();
        GetBookingByIdResponse response = GetBookingByIdResponse.builder()
                .booking(booking)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{id}")
    ResponseEntity<GetUsersBookingResponse> getUsersBooking(@PathVariable("id") long id){
        Optional<User> optUser = userService.getUser(id);
        if(optUser.isEmpty())
            return ResponseEntity.notFound().build();

        User user = optUser.get();
        List<Booking> bookings = bookingService.getUsersBooking(user);
        GetUsersBookingResponse response = GetUsersBookingResponse.builder()
                .bookings(bookings)
                .build();
        return ResponseEntity.ok(response);

    }

    @PostMapping
    ResponseEntity<CreateBookingResponse> createBooking(@RequestBody CreateBookingRequest request){
        Optional<User> optStudent = userService.getUser(request.getStudentId());
        if(optStudent.isEmpty() || optStudent.get().getRole() != Role.Student)
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();
        Optional<User> optTutor = userService.getUser(request.getTutorId());
        if(optTutor.isEmpty() || optTutor.get().getRole() != Role.Tutor)
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();

        Student student = (Student)optStudent.get();
        Tutor tutor = (Tutor)optTutor.get();


        Booking newBooking = Booking.builder()
                .dateAndTime(request.getDateAndTime())
                .description(request.getDescription())
                .tutor(tutor)
                .student(student)
                .build();
        newBooking = bookingService.createBooking(newBooking);
        CreateBookingResponse response = CreateBookingResponse.builder()
                .booking(newBooking)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping
    ResponseEntity<RescheduleBookingResponse> rescheduleBooking(@RequestBody RescheduleBookingRequest request){
        Optional<Booking> optNewBooking = bookingService.rescheduleBooking(request.getBooking());
        if(optNewBooking.isEmpty())
            return ResponseEntity.unprocessableEntity().build();

        Booking newBooking = optNewBooking.get();
        RescheduleBookingResponse response = RescheduleBookingResponse.builder()
                .booking(newBooking)
                .build();
        return ResponseEntity.ok(response);
    }
}
