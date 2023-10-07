package com.smartbyte.edubookschedulerbackend.business.response;

import com.smartbyte.edubookschedulerbackend.domain.User;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateUserResponse {
    User user;
}
