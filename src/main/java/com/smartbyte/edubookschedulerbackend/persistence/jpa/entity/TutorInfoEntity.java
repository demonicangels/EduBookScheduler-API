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
public class TutorInfoEntity extends UserEntity {
    
    @ManyToMany(mappedBy = "tutors")
    @JoinTable(
            name = "assign_tutor",
            joinColumns = @JoinColumn(name = "tutorInfo_id"),
            inverseJoinColumns =@JoinColumn(name = "studentInfo_id")
    )
    List<StudentInfoEntity> students;
}
