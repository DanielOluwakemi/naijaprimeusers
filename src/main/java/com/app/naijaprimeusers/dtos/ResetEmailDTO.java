package com.app.naijaprimeusers.dtos;

import lombok.Data;

@Data
public class ResetEmailDTO {

    private String username;
    private String email;
    private String newEmail;
}
