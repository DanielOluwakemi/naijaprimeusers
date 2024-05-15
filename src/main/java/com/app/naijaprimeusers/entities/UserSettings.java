package com.app.naijaprimeusers.entities;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
public class UserSettings {

    @Id
    private String id;
    private String userID;
    private String theme;
    private String palette;
    private String isolationMade;
    private int signOut; // 0 for manually, 1 for auto
    private int deleteFlag;
}
