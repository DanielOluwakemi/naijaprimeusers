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
    private int userType; // 0 for viewer, 1 for Content creator, 2 for staff
    private int code;
    private long codeCreatedTime;
    private boolean verified;
    private long createdTime;
    private String token;
    private int deleteFlag;
}
