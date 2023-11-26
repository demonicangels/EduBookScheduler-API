package com.smartbyte.edubookschedulerbackend.business;

import com.smartbyte.edubookschedulerbackend.business.request.GetAvailabilityRequest;
import com.smartbyte.edubookschedulerbackend.business.response.GetAvailabilityResponse;
import com.smartbyte.edubookschedulerbackend.business.response.GetAvailabilityTutorResponse;
import com.smartbyte.edubookschedulerbackend.business.response.GetUsersResponse;
import com.smartbyte.edubookschedulerbackend.domain.Booking;
import com.smartbyte.edubookschedulerbackend.domain.User;


import java.util.List;

public interface AvailabilityService {
    List<GetAvailabilityResponse> findAvailableTeachersByDateAndTime(GetAvailabilityRequest request);
    GetUsersResponse GetTutors ();
    GetAvailabilityTutorResponse getTutorsBooking(long id);
}
