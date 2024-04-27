package com.app.naijaprimeusers.environment;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class APIs {

    @Value("${service.name}")
    public String serviceName;
    @Value("${forgot-password.url}")
    private String forgotPasswordUrl;
    @Value("${go-mailer.apiKey}")
    private String goMailerApiKey;
    @Value("${go-mailer.templateCode}")
    private String goMailerTemplateCode;
    @Value("${go-mailer.sendApi}")
    private String goMailerSendApi;
    @Value("${no-reply.email}")
    private String noReply;
}
