package com.smartbyte.edubookschedulerbackend.domain;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tutor extends User {
    @Override
    public Role getRole() {
        return Role.Tutor;
    }
}