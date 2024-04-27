package com.app.naijaprimeusers.dtos;

import lombok.Data;

@Data
public class MessageDTO {

    private String id;
    private String FullName;
    private String Email;
    private String loginURL;
    private String password;
    private int code;
}
