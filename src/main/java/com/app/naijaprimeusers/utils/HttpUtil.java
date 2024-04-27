package com.app.naijaprimeusers.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Slf4j
@Component
public class HttpUtil {


    @Autowired
    RestTemplate restTemplate;

    public String getObject(Map<String, String> myheaders, String url) {
        log.info("Get API Call");
        //Calling Get API
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/json");
        headers.set("content-type","application/json");
        for (var entry : myheaders.entrySet()) {
            headers.set(entry.getKey(), entry.getValue());
        }
        HttpEntity<String> entity = new HttpEntity<String>(headers);
        log.info("Entity: "+entity.toString());

        ResponseEntity<String> result = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        return result.getBody();
    }

    public String postObject(String object, Map<String, String> myheaders, String url) {
        log.info("Post API");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/json");
        headers.set("content-type","application/json");
        for (var entry : myheaders.entrySet()) {
            headers.set(entry.getKey(), entry.getValue());
        }
        HttpEntity<String> entity = new HttpEntity<String>(object, headers);
        log.info("Entity: "+entity.toString());

        String response = restTemplate.postForObject(url, entity, String.class);
        log.info("Response : "+response);
        return response;
    }
}
