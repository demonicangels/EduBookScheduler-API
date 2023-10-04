package com.smartbyte.edubookschedulerbackend.domain;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class User {
    Long id;
    String name;
    String email;
    @Setter(AccessLevel.NONE) //readonly
    Role role;
}
