package com.smartbyte.edubookschedulerbackend.business.response;

import com.smartbyte.edubookschedulerbackend.domain.User;
import lombok.Builder;
import lombok.Data;

import java.util.*;

@Data
@Builder
public class GetUsersResponse {
    List<User> users;

}
