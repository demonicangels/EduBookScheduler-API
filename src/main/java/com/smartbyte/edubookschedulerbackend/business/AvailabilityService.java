package com.smartbyte.edubookschedulerbackend.business;

import com.smartbyte.edubookschedulerbackend.business.request.CreateSetAvailabilityRequest;
import com.smartbyte.edubookschedulerbackend.business.request.GetAvailabilityRequest;
import com.smartbyte.edubookschedulerbackend.business.request.GetSetSetAvailabilityRequest;
import com.smartbyte.edubookschedulerbackend.business.response.*;
import com.smartbyte.edubookschedulerbackend.domain.Booking;
import com.smartbyte.edubookschedulerbackend.domain.User;


import java.util.List;

public interface AvailabilityService {
    List<GetAvailabilityResponse> findAvailableTeachersByDateAndTime(GetAvailabilityRequest request);
    GetUsersResponse GetTutors ();
    GetTutorsNameResponse GetTutorsName(long id);
    GetAvailabilityTutorResponse getTutorsBooking(long id);
    List<CreateSetAvailabilityResponse> createAvailability(List<CreateSetAvailabilityRequest> requests);
    List<GetSetAvailabilityResponse> getAvailabilityOfTutorWeekly(long id);
}
