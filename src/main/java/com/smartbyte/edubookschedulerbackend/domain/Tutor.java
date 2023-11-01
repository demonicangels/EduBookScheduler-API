package com.smartbyte.edubookschedulerbackend.domain;

import jakarta.persistence.Entity;
import lombok.*;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@SuperBuilder
@Entity
public class Tutor extends User {
    @Override
    public Role getRole() {
        return Role.Tutor;
    }
}