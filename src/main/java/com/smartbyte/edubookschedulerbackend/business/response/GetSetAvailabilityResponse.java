package com.smartbyte.edubookschedulerbackend.business.response;

import com.smartbyte.edubookschedulerbackend.domain.AvailabilityDomain;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetSetAvailabilityResponse {
    AvailabilityDomain availabilityDomain;
}
