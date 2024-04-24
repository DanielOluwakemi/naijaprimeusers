package com.app.naijaprimeusers.dtos;

import lombok.Data;

@Data
public class ResponseDTO {

    private String status;
    private String message;
    private Object data;
}
