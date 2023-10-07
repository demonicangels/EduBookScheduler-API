package com.smartbyte.edubookschedulerbackend.domain;

import lombok.*;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@SuperBuilder
public class Student extends User {
    private long PCN;
    @Override
    public Role getRole() {
        return Role.Student;
    }
}
