package com.smartbyte.edubookschedulerbackend.domain;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Availability {
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    Date date;
    int startTime;
    int endTime;
}
