package com.app.naijaprimeusers.services;

import com.app.naijaprimeusers.dtos.MailDTO;
import com.app.naijaprimeusers.dtos.SendMailDTO;
import com.app.naijaprimeusers.environment.APIs;
import com.app.naijaprimeusers.utils.HttpUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service("awsMailSender")
public class GoMailerService {

    @Autowired
    APIs apis;
    @Autowired
    HttpUtil httpUtil;
    @Autowired
    ObjectMapper objectMapper;

    @Async
    public void sendEmail(MailDTO mailDTO) throws JsonProcessingException {
        log.info("Sending Email {}" , mailDTO);

        SendMailDTO sendMailDTO = new SendMailDTO();
        sendMailDTO.setHtml(mailDTO.getMsg());
        sendMailDTO.setRecipient_email(mailDTO.getTo());
        sendMailDTO.setTemplate_code(apis.getGoMailerTemplateCode());
        sendMailDTO.setData(new SendMailDTO.MailData());
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + apis.getGoMailerApiKey());
        httpUtil.postObject(objectMapper.writeValueAsString(sendMailDTO), headers, apis.getGoMailerSendApi());

        log.info("Sent Email Successfully");
    }
}
