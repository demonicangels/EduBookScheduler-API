package com.smartbyte.edubookschedulerbackend.persistence.jpa.entity;

import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@DiscriminatorValue("1")
@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
public class TutorInfoEntity extends UserEntity {
}
