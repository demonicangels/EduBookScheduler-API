package com.smartbyte.edubookschedulerbackend.domain;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class User {
    Long id;
    String name;
    String email;
    String password;
    @Setter(AccessLevel.NONE) //readonly
    Role role;
}
