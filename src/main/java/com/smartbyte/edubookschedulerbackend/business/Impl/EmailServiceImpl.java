package com.smartbyte.edubookschedulerbackend.business.Impl;

import com.smartbyte.edubookschedulerbackend.business.EmailService;
import com.smartbyte.edubookschedulerbackend.business.request.SendEmailRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender javaMailSender;
    private String recipient = "nikolgeova@gmail.com";
    private String sender;

    @Autowired
    public EmailServiceImpl(JavaMailSender javaMailSender, @Value("spring.mail.username") String sender){
        this.javaMailSender = javaMailSender;
        this.sender = sender;
    }
    @Override
    public String sendEmail(SendEmailRequest request) {
        try{
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(recipient);
            mailMessage.setFrom(sender);
            mailMessage.setSubject("Booking confirmation");
            mailMessage.setText(request.getMessage());

            javaMailSender.send(mailMessage);

        }catch (Exception err){
            throw err;
        }
        return "Successful confirmation email";
    }
}
