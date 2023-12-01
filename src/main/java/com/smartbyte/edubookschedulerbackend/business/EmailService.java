package com.smartbyte.edubookschedulerbackend.business;

import com.smartbyte.edubookschedulerbackend.business.request.SendEmailRequest;

public interface EmailService {
    String sendEmail(SendEmailRequest request);
}
