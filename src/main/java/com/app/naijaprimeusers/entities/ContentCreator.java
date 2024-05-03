package com.app.naijaprimeusers.entities;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
public class ContentCreator {

    @Id
    private String id;
    private String email;
    private String age;
    private String phoneNumber;
    private String fullName;
    private String prodName;
    private String filmProdLocation;
    private String membership;
    private AccountDetails accountDetails;
    private File file;
    private long createdTime;
    private int deleteFlag;

    @Data
    public static class AccountDetails {
        private String fullName;
        private String acctNo;
        private String bankDetails;
    }

    @Data
    public static class File {
        private String imageID;
        private String imageUrl;
    }
}
