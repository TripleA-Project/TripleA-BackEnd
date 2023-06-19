package com.triplea.triplea.core.util;

import com.triplea.triplea.core.exception.Exception400;
import com.triplea.triplea.core.exception.Exception500;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.internet.MimeMessage;

@Component
public class MailUtils {
    private static MailProperties mailProperties;
    private static JavaMailSender mailSender;

    @Autowired
    public MailUtils(MailProperties mailProperties, JavaMailSender mailSender) {
        MailUtils.mailProperties = mailProperties;
        MailUtils.mailSender = mailSender;
    }

    public void send(String to, String subject, String contents){
        if(to.isEmpty()) return;
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(message, false, "UTF-8");

            messageHelper.setSubject(subject);
            messageHelper.setText(contents,true);
            messageHelper.setFrom(mailProperties.getFromMail());

            String[] toArr = to.split(",");

            messageHelper.setTo(toArr);

            mailSender.send(message);
        } catch (MailException es){
            throw new Exception400("email","email 전송 실패");
        }catch (Exception e){
            throw new Exception500("email 전송 실패: "+e.getMessage());
        }
    }
}
