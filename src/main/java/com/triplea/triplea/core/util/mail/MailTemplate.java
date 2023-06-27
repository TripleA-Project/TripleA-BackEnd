package com.triplea.triplea.core.util.mail;

import org.springframework.stereotype.Component;

@Component
public class MailTemplate {

    private static final String URL = "https://stock.moya.ai/";
    private static final String htmlTop = "<table style='width: 100%; padding:32px 0 32px 0; background-color: #f3f3f3'>" +
            "    <tbody>" +
            "    <tr>" +
            "        <td>" +
            "            <table style='margin:auto; width: 600px; padding:0 20px 0 20px; background-color: #fff; font-family: Arial,SansSerif;'>" +
            "                <tbody>" +
            "                <tr>" +
            "                    <td style='padding:20px 0; border-bottom: 1px solid #ddd'>" +
            "                        Triple A" +
            "                    </td>" +
            "                </tr>";
    private static final String htmlBottom =
            "                <tr>" +
                    "                    <td style='padding:20px 0; border-top: 1px solid #ddd;'>" +
                    "                        <p style='padding: 0; margin: 0; color: #aaa; font-size: 12px'>Copyright © 2021 Sysmetic.All rights reserved.</p>" +
                    "                    </td>" +
                    "                </tr>" +
                    "                </tbody>" +
                    "            </table>" +
                    "        </td>" +
                    "    </tr>" +
                    "    </tbody>" +
                    "</table>";

    public static String sendEmailVerificationCodeTemplate(String code) {
        String title = "이메일 인증해주세요.";
        String content = "<p style='margin: 40px 0; font: 24px bold; color: red; text-align: center'>" + code + "</p><p style='font-size: 14px;'>해당 인증코드는 3분간 유효합니다.</p>";
        String link = URL + "";
        String button = "Triple A로 이동하기";
        return htmlTop + template(title, content, link, button) + htmlBottom;
    }

    public static String sendNewPasswordEmailTemplate(String password) {
        String title = "새로운 비밀번호가 발급되었습니다.";
        String content = "<p style='margin: 40px 0; font: 24px bold; color: red; text-align: center'>" + password + "</p><p style='font-size: 14px;'>개인정보 수정에서 비밀번호를 변경해주세요.</p>";
        String link = URL + "";
        String button = "Triple A로 이동하기";
        return htmlTop + template(title, content, link, button) + htmlBottom;
    }

    public static String sendJoinTemplate() {
        String title = "가입을 환영합니다.";
        String content = "<p style='margin: 40px 0'>지금부터 이 계정으로 Triple A를 이용하실 수 있습니다.</p>";
        String link = URL + "";
        String button = "Triple A로 이동하기";
        return htmlTop + template(title, content, link, button) + htmlBottom;
    }

    private static String template(String title, String contents, String link, String button) {
        return "<tr>" +
                "                    <td style='padding:20px 0'>" +
                "                        <h1 style='font: 20px bold'>" + title + "</h1>" + contents +
                "                        <a style='display: block; text-decoration: none; color: #fff; background-color: #FD954A; margin: 0; padding: 20px 10px; font: 14px bold; text-align: center;' href='" + link + "'>" + button + "</a>" +
                "                    </td>" +
                "                </tr>";
    }
}
