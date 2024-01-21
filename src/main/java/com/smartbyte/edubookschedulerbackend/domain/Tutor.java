package com.smartbyte.edubookschedulerbackend.domain;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@SuperBuilder
public class Tutor extends User {
    @Override
    public Role getRole() {
        return Role.Tutor;
    }

    @EqualsAndHashCode.Exclude
    private List<Student> assignedStudents;

}