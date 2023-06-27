package com.triplea.triplea.core.util.mail;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties("application.mail")
public class MailProperties {
    private String host;
    private String username;
    private String password;
    private int port;
    private String supplier;
    private String fromMail;
    private String socketFactoryClass;
}
