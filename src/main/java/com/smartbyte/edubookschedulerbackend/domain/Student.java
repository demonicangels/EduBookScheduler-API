package com.smartbyte.edubookschedulerbackend.domain;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

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

    @EqualsAndHashCode.Exclude
    private List<Tutor> assignedTutors;
}
