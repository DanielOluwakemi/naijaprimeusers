package com.app.naijaprimeusers.utils;

import com.app.naijaprimeusers.dtos.MessageDTO;
import org.springframework.stereotype.Component;

@Component
public class MessageTemplates {
    public String getTemplate(int type, MessageDTO messageDTO) {
        String msg = "";

        if(type == 1) {//Registration Email verification
            msg += "<p>Hello " + messageDTO.getFullName() + ",</p>";
            msg += "<p>Thank you for signing up with Naija Prime! </p>";
            msg += "<p>To complete your registration, please use the following verification code:</p>";
            msg += "<p>Verification Code: " + messageDTO.getCode() + "<p>";
            msg += "<p>Please enter this code on our website to verify your email address and activate your account. Note that this code will expire in 30 minutes<p>";
            msg += "<p>If you did not request this verification or have any questions, please contact our support team at naijaprimesupport@gmail.com.</p>";
        } else if (type == 2) {
            msg += "<p>Hello " + messageDTO.getFullName() + ",</p>";
            msg += "<p>You have successfully changed your email</p>";
            msg += "<p>To complete your change of email, please use the following verification code:</p>";
            msg += "<p>Verification Code: " + messageDTO.getCode() + "<p>";
            msg += "<p>Please enter this code on our website to verify your email address and activate your account. Note that this code will expire in 30 minutes<p>";
            msg += "<p>If you did not request this verification or have any questions, please contact our support team at naijaprimesupport@gmail.com.</p>";
        }
        msg += "<p><br>Regards,</p>";
        msg += "<p>Naija Prime Team</p>";

        return msg;
    }

    public String getSMSTemplate(int type, String name) {
        String msg = "";

        if(type == 1) {//Birthdays
            msg += "Dear " + name + " On a day of celebration like this, everyone from the TechEveryWhere Team wishes you a happy birthday. Do enjoy your day.";
        }

        return msg;
    }}

