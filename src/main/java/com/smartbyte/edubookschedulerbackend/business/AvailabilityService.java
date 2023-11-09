package com.smartbyte.edubookschedulerbackend.business;

import com.smartbyte.edubookschedulerbackend.business.request.GetAvailabilityRequest;
import com.smartbyte.edubookschedulerbackend.business.response.GetAvailabilityResponse;


import java.util.List;

public interface AvailabilityService {
    List<GetAvailabilityResponse> findAvailableTeachersByDateAndTime(GetAvailabilityRequest request);
}
