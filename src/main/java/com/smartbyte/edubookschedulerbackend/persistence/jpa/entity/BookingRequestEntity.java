package com.smartbyte.edubookschedulerbackend.persistence.jpa.entity;

import com.smartbyte.edubookschedulerbackend.domain.BookingRequestAnswer;
import com.smartbyte.edubookschedulerbackend.domain.BookingRequestType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "booking_request")
public class BookingRequestEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Enumerated(EnumType.ORDINAL)
    @NotNull
    @Column(name = "request_type")
    private BookingRequestType requestType;

    @NotNull
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "requester_id")
    @EqualsAndHashCode.Exclude
    private UserEntity requester;
    @NotNull
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "receiver_id")
    @EqualsAndHashCode.Exclude
    private UserEntity receiver;

    @NotNull
    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "booking_to_schedule_id")
    @EqualsAndHashCode.Exclude
    private BookingEntity bookingToSchedule;

    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "booking_to_reschedule_id")
    @EqualsAndHashCode.Exclude
    private BookingEntity bookingToReschedule;

    @Enumerated(EnumType.ORDINAL)
    @NotNull
    @Column(name = "answer")
    private BookingRequestAnswer answer;

}
