package com.smartbyte.edubookschedulerbackend.business;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
@PropertySource("classpath:mail.properties")
public class MailConfig {

    private Integer port;
    private String host;
    private String username;
    private String password;

    @Autowired
    public MailConfig(@Value("${spring.mail.port}") Integer port,
                      @Value("${spring.mail.host}") String emailHost,
                      @Value("${spring.mail.username}") String username,
                      @Value("${spring.mail.password}") String password){

        this.port = port;
        this.host = emailHost;
        this.username = username;
        this.password = password;

    }

    @Bean
    public JavaMailSender javaMailSender(){
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(host);
        mailSender.setPort(port);
        mailSender.setUsername(username);
        mailSender.setPassword(password);

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.timeout", "5000");
        props.put("mail.debug", "true");

        return mailSender;
    }
}
