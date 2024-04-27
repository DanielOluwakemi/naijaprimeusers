package com.app.naijaprimeusers.dtos;

import lombok.Data;

@Data
public class MailDTO {

    private String to;
    private String msg;
    private String subject;
    private String cc;
    private String bcc;
    private String from;
    private String receiverName;
}
