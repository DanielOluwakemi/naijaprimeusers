package com.app.naijaprimeusers.dtos;

import lombok.Data;

@Data
public class AccessDTO {

    private String username;
    private String password;
    private int userType;
    private String newPassword;
}

