package com.smartbyte.edubookschedulerbackend.business.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class SearchAssignedUserByNameRequest {
    private long studentId;
    private String name;
}
