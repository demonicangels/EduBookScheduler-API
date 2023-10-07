package com.smartbyte.edubookschedulerbackend.business.response;

import com.smartbyte.edubookschedulerbackend.domain.User;
import lombok.Builder;
import lombok.Data;

import java.util.Optional;

@Data
@Builder
public class UpdateUserResponse {
    Optional<User> user;
}
