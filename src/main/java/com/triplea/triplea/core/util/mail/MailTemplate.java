package com.triplea.triplea.core.util.mail;

import org.springframework.stereotype.Component;

@Component
public class MailTemplate {

    private static final String URL = "https://stock.moya.ai/";

    private static String getHtmlTop(String description) {
        return "<table style='width: 100%; margin:0; padding:32px 0 32px 0; background-color: #f3f3f3'>" +
                "    <tbody>" +
                "    <tr>" +
                "        <td>" +
                "           <table style='margin:0 auto; width: 600px; padding:0 20px 0 20px; background-color: #fff; font-family: Arial,SansSerif;'>" +
                "                <tbody>" +
                "                <tr>" +
                "                    <td style='border-bottom:2px solid #FF9243; padding: 20px 0; width: 100%; font-size: 0;'>" +
                "                        <div style='display: inline-block; width: 40%; vertical-align: middle; margin:0; padding:0;'>" +
                "                            <img src='https://imagedelivery.net/gDNaP20ZP5HjgdRwMYWErw/6d3b0669-12af-4f5a-ae1c-646535aa4a00/public' width='24.25' height='21' style='display:inline-block;vertical-align:middle;outline:none;border:none;text-decoration:none;font-size:0;'/>" +
                "                            <p style='display:inline-block; vertical-align: middle; font:14px bold; margin:0; padding:0;'>Triple A</p>" +
                "                        </div>" +
                "                        <p style='display: inline-block; vertical-align: middle; font-size: 14px; width: 59%; text-align: right; margin:0; padding:0;'>" + description + "</p>" +
                "                    </td>" +
                "                </tr>";
    }

    private static final String htmlBottom = "<tr>" +
            "                    <td style='padding:20px 0; border-top: 1px solid #ddd;'>" +
            "                        <p style='padding: 0; margin: 0; color: #aaa; font-size: 12px'>Copyright © 2021 Sysmetic.All rights reserved.</p>" +
            "                    </td>" +
            "                </tr>" +
            "                </tbody>" +
            "            </table>" +
            "        </td>" +
            "    </tr>" +
            "    </tbody>" +
            "</table>" +
            "        </td>" +
            "    </tr>" +
            "    </tbody>" +
            "</table>";

    public static String sendEmailVerificationCodeTemplate(String code) {
        String title = "이메일 인증해주세요.";
        String description = "인증번호 안내";
        String contents = "아래 인증번호를 인증번호 입력창에 입력해주세요.";
        String button = "Triple A로 이동하기";
        return getHtmlTop(description) + template(title, contents, code, true, URL, button) + htmlBottom;
    }

    public static String sendNewPasswordEmailTemplate(String password) {
        String title = "새로운 비밀번호가 발급되었습니다.";
        String description = "비밀번호 발급";
        String contents = "개인정보 수정에서 비밀번호를 변경해주세요.";
        String link = URL + "me/edit/password";
        String button = "비밀번호 수정하기";
        return getHtmlTop(description) + template(title, contents, password, false, link, button) + htmlBottom;
    }

    public static String sendJoinTemplate(String email) {
        String title = "가입을 환영합니다.";
        String description = "회원가입";
        String contents = "지금부터 이 계정으로 Triple A를 이용하실 수 있습니다.";
        String button = "Triple A로 이동하기";
        return getHtmlTop(description) + template(title, contents, email, false, URL, button) + htmlBottom;
    }

    private static String template(String title, String contents, String code, boolean isEmailVerify, String link, String button) {
        String htmlTop = "<tr>" +
                "                    <td style='padding:20px 0;'>" +
                "                        <h1 style='font: 20px bold; margin:0; padding:20px 0;'>" + title + "</h1>" +
                "                        <div style='padding: 20px 0; margin:0 0 20px; text-align: center; border:1px solid #ccc;'>" +
                "                            <p style='color:#555; font-size: 14px; margin:0; padding:20px 0;'>" + contents + "</p>" +
                "                            <p style='font: 24px bold; color: #FF9243; margin:0; padding:40px 0 20px;'>" + code + "</p>" +
                "                        </div>";
        String desc = "<p style='font-size: 14px; padding:0; margin:0; color:#000;'>해당 인증코드는 3분간 유효합니다.</p>";
        String htmlBottom = "<a style='display: block; text-decoration: none; color: #fff; background-color: #FD954A; margin: 20px 0 0; padding: 20px 10px; font-family: inherit; font: 14px bold; text-align: center;' href='" + link + "'>" + button + "</a>" +
                "                    </td>" +
                "                </tr>";
        if (isEmailVerify) return htmlTop + desc + htmlBottom;
        return htmlTop + htmlBottom;
    }
}
