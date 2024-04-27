package com.app.naijaprimeusers.dtos;

import lombok.Data;

@Data
public class LoginResponseDTO {

    //private fields
    private String status;
    private String message;
    private Object data;
    private int userType; // 0 for viewer, 1 for Content creator, 2 for staff
    private boolean wishBirthday;
}
