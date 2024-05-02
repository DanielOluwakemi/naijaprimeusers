package com.app.naijaprimeusers.dtos;

import lombok.Data;

@Data
public class MediaDTO {

    private String id;
    private String name;
    private String displayName;
    private String key;
    private String type;
    private String fileName;
    private long createdTime;
    private int deleteFlag;
}
