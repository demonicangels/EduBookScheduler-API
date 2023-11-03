package com.smartbyte.edubookschedulerbackend.persistence.jpa.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "booking")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "date")
    private Date date;

    /*
        start and end time are defined as the
        number of minutes since midnight.
        therefore:
            time = 60*hour + minute;
        thus:
            hour = time / 60 (int division) and
            minute = time % 60
     */

    @NotNull
    @Column(name = "startTime")
    @Min(0)
    @Max(1439)
    private Integer startTime;

    @NotNull
    @Column(name = "endTime")
    @Min(0)
    @Max(1439)
    private Integer endTime;

    @NotEmpty
    @Column(name = "description")
    private String description;

    @ManyToOne
    @JoinColumn(name = "student_id")
    @EqualsAndHashCode.Exclude
    private UserEntity student;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "tutor_id")
    @EqualsAndHashCode.Exclude
    private UserEntity tutor;

}
