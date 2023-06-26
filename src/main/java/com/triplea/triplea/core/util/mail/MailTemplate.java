package com.triplea.triplea.core.util.mail;

import org.springframework.stereotype.Component;

@Component
public class MailTemplate {

    public static String sendEmailVerificationCodeTemplate(String code){
        return "<div>인증코드: " + code + "<p style='font-weight:bold;'>해당 인증코드는 3분간 유효합니다.</p></div>";
    }

    public static String sendNewPasswordEmailTemplate(String password){
        return "<div>비밀번호: " + password + "<p style='font-weight:bold;'>개인정보 수정에서 비밀번호를 변경해주세요.</p></div>";
    }

    public static String sendJoinTemplate(){
        return "<div>가입을 환영합니다.</div>";
    }
}
