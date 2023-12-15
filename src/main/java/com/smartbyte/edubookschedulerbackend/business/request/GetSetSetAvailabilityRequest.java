package com.smartbyte.edubookschedulerbackend.business.request;

import jakarta.validation.constraints.Min;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetSetSetAvailabilityRequest {
    @Min(1)
    Long id;
}
