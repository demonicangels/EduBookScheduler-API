package com.smartbyte.edubookschedulerbackend.persistence.jpa.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import java.util.List;

@Entity
@Table(name = "edu_user")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotEmpty
    @Column(name = "name")
    @Length(min = 2, max = 50)
    private String name;

    @NotEmpty
    @Column(name = "email")
    @Length(min = 5)
    private String email;

    @NotEmpty
    @Length(min = 5)
    @Column(name = "password")
    private String password;

    @NotNull
    @Column(name = "role")
    private Integer role;

    @OneToMany(mappedBy = "student")
    @EqualsAndHashCode.Exclude
    private List<BookingEntity> studentBookings;

    @OneToMany(mappedBy = "tutor")
    private List<BookingEntity> tutorBookings;
}
