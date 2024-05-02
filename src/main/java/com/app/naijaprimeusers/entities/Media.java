package com.app.naijaprimeusers.entities;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
public class Media {

    @Id
    private String id;
    private String name;
    private String displayName;
    private String key;
    private String type;
    private long createdTime;
    private int deleteFlag;
}
