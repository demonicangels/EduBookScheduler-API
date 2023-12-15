package com.smartbyte.edubookschedulerbackend.persistence.jpa.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "availability")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AvailabilityEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "date")
    private Date date;

    @NotNull
    @Column(name = "startTime")
    @Min(0)
    @Max(9999)
    private Integer startTime;

    @NotNull
    @Column(name = "endTime")
    @Min(0)
    @Max(9999)
    private Integer endTime;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "tutor_id")
    @EqualsAndHashCode.Exclude
    private UserEntity tutor;
}
