package com.app.naijaprimeusers.dtos;

import lombok.Data;

@Data
public class LoginResponseDTO {

    //private fields
    private String status;
    private String message;
    private Object data;
    private int userType;
    private boolean wishBirthday;
}
