package com.smartbyte.edubookschedulerbackend.persistence.jpa.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Entity
@DiscriminatorValue("1")
@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TutorInfoEntity extends UserEntity {

    @ManyToMany
    @JoinTable(
            name = "assign_tutor",
            joinColumns = @JoinColumn(name = "tutorInfo_id"),
            inverseJoinColumns = @JoinColumn(name = "studentInfo_id")
    )
    private List<StudentInfoEntity> students;
}
