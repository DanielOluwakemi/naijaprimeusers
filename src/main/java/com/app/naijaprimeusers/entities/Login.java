package com.app.naijaprimeusers.entities;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
public class Login {

    @Id
    private String id;
    private String username;
    private String password;
    private int userType;
    private int deleteFlag;
    private long createdTime;
    private String token;
}
