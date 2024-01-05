package com.smartbyte.edubookschedulerbackend.persistence.jpa.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Entity
@DiscriminatorValue("0")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class StudentInfoEntity extends UserEntity{
    @Column(name = "pcn")
    private Long pcn;

    @ManyToMany(mappedBy = "students")
    private List<TutorInfoEntity> tutors;
}
