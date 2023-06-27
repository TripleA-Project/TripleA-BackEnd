package com.triplea.triplea.core.util.mail;

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

    public enum MailType{
        CODE, PASSWORD, JOIN
    }

    public void send(String to, MailType type, String contents){
        if(to.isEmpty()) return;

        String subject;
        switch (type) {
            case CODE:
                subject = "[Triple A] 이메일 인증을 진행해주세요.";
                break;
            case PASSWORD:
                subject = "[Triple A] 새로운 비밀번호 발급";
                break;
            case JOIN:
                subject = "[Triple A] 가입을 환영합니다.";
                break;
            default:
                // 기본값 처리 또는 예외 상황에 대한 처리
                subject = "[Triple A] 이메일 알림";
                break;
        }

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
