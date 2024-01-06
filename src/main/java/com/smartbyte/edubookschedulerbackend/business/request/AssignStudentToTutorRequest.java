package com.smartbyte.edubookschedulerbackend.business.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class AssignStudentToTutorRequest {
    private long tutorId;
    private List<Long> studentIds;
}
