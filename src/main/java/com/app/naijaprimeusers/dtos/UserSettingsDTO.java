package com.app.naijaprimeusers.dtos;

import lombok.Data;

@Data
public class UserSettingsDTO {

    private String id;
    private String userID;
    private String theme;
    private String palette;
    private String userFullName;
    private String userProdName;
    private Security security;
    private PaymentDetails paymentDetails;
    private int deleteFlag;

    @Data
    public static class Security {
        private String isolationMade;
        private int signOut; // 0 for manually, 1 for auto
    }

    @Data
    public static class PaymentDetails {
        private String fullName;
        private String acctNo;
        private String bankName;
    }
}
