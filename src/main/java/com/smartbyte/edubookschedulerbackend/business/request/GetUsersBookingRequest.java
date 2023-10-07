package com.smartbyte.edubookschedulerbackend.business.request;

import com.smartbyte.edubookschedulerbackend.domain.Booking;
import com.smartbyte.edubookschedulerbackend.domain.User;
import lombok.Builder;
import lombok.Data;

import java.util.*;

@Data
@Builder
public class GetUsersBookingRequest {
    User user;
}
