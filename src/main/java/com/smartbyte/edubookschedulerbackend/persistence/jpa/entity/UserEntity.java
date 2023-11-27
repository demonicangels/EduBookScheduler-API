package com.smartbyte.edubookschedulerbackend.persistence.jpa.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.validator.constraints.Length;

import java.util.List;

@Entity
@Table(name = "edu_user")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "role", discriminatorType = DiscriminatorType.INTEGER)
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotEmpty
    @Column(name = "name")
    @Length(min = 2)
    private String name;

    @NotEmpty
    @Column(name = "email")
    @Length(min = 5)
    private String email;

    @NotEmpty
    @Column(name = "password")
    private String password;

    @Lob
    @Column(name = "profile_picurl")
    private String profilePicURL;

    @NotNull
    @Column(name = "role", insertable = false, updatable = false)
    private Integer role;

    @OneToMany(mappedBy = "student")
    @EqualsAndHashCode.Exclude
    private List<BookingEntity> studentBookings;

    @OneToMany(mappedBy = "tutor")
    private List<BookingEntity> tutorBookings;
}
