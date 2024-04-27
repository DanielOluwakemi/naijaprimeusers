package com.app.naijaprimeusers.dtos;

import lombok.Data;

@Data
public class SendMailDTO {

    private String template_code;
    private String html;
    private String recipient_email;
    private MailData data;


    @Data
    public static class MailData {
        private String mail;
    }
}
