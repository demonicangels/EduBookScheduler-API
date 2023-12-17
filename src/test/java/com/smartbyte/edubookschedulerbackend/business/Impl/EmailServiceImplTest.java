package com.smartbyte.edubookschedulerbackend.business.Impl;

import com.smartbyte.edubookschedulerbackend.business.request.SendEmailRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EmailServiceImplTest {

    @Mock
    private JavaMailSender javaMailSender;

    private String recipient;

    private String sender;

    @InjectMocks
    private EmailServiceImpl emailService;

    @BeforeEach
    public void init(){
        this.recipient="n.genova@student.fontys.nl";
        this.sender="n.genova@student.fontys.nl";
        emailService=new EmailServiceImpl(javaMailSender,sender);
    }

    /**
     * @verifies send email
     * @see EmailServiceImpl#sendEmail(com.smartbyte.edubookschedulerbackend.business.request.SendEmailRequest)
     */
    @Test
    void sendEmail_shouldSendEmail() {
        //Arrange
        SendEmailRequest request=SendEmailRequest.builder().build();

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(recipient);
        mailMessage.setFrom(sender);
        mailMessage.setSubject(request.getSubject());
        mailMessage.setText(request.getMessage());

        //Act
        emailService.sendEmail(request);

        //Assert
        verify(javaMailSender).send(mailMessage);

    }
}
