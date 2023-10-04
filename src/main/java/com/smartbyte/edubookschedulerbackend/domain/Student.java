package com.smartbyte.edubookschedulerbackend.domain;

import lombok.*;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Student extends User {
    @Override
    public Role getRole() {
        return Role.Student;
    }
}
