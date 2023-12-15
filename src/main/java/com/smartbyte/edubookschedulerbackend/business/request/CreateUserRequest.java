package com.smartbyte.edubookschedulerbackend.business.request;

import com.smartbyte.edubookschedulerbackend.domain.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
@Data
@Builder
public class CreateUserRequest {
    @Size(min = 3)
    String name;
    @NotBlank
    String email;
    @NotBlank
    String password;
    @NotBlank
    String profilePicURL;
    @NonNull
    Role role;
}
