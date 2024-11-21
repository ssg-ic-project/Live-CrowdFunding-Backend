package com.crofle.livecrowdfunding.service.serviceImpl;


import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class EmailService {
    @Autowired
    private JavaMailSender emailSender;

    //메일 보내기
    public void sendEmail(String to, String subject, String text){
        try{
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);

            emailSender.send(message);
            log.info("Email sent successfully to: {}", to);
        }catch(MailException e){
            log.error("Failed to send email to: {}", to, e);
            throw new RuntimeException("이메일 발송에 실패했습니다.", e);
        }
    }
}
