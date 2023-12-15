package com.smartbyte.edubookschedulerbackend.business.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetTutorsNameResponse {
    String name;
}
