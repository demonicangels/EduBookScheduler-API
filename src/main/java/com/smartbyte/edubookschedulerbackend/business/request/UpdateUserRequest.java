package com.smartbyte.edubookschedulerbackend.business.request;

import com.smartbyte.edubookschedulerbackend.domain.User;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateUserRequest {
    User user;
}
