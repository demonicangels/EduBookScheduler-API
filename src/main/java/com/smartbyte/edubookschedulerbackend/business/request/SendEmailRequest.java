package com.smartbyte.edubookschedulerbackend.business.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class SendEmailRequest {
    private final String message;
    private final String subject;
}
