package com.app.naijaprimeusers.entities;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
public class ContentCreator {

    @Id
    private String id;
    private String username;
    private String email;
    private String fullName;
    private String age;
    private long createdTime;
    private int deleteFlag;
}
