package com.smartbyte.edubookschedulerbackend.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;
    Date dateAndTime;
    String description;

    @ManyToOne
    private User user;
//
//    @ManyToOne(cascade = CascadeType.ALL)
//    Student student;
//    Tutor tutor;
}
