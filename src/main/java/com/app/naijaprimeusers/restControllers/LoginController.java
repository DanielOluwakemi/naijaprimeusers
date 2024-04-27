package com.app.naijaprimeusers.restControllers;

import com.app.naijaprimeusers.dtos.AccessDTO;
import com.app.naijaprimeusers.dtos.LoginResponseDTO;
import com.app.naijaprimeusers.dtos.ResponseDTO;
import com.app.naijaprimeusers.entities.Login;
import com.app.naijaprimeusers.services.LoginService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@CrossOrigin(origins = "*")
@Tag(name = "Login Endpoints", description = "These endpoints manages user logins on naijaprimeusers")
@RequestMapping(path = "/login")
@RestController("LoginController")
public class LoginController {


    @Autowired
    private LoginService loginService;

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @Operation(description = "This Service creates a new login")
    public ResponseEntity<?> add(@RequestBody Login login) {
        log.info("API Call To Add New login");

        try {
            ResponseDTO response = loginService.add(login);
            if (response.getStatus().equalsIgnoreCase("SUCCESS")) {
                return new ResponseEntity<>(response, HttpStatus.CREATED);
            } else if (response.getStatus().equalsIgnoreCase("EMPTY_TEXTFIELD")) {
                return new ResponseEntity<>(response, HttpStatus.PRECONDITION_REQUIRED);
            } else {
                return new ResponseEntity<>(response, HttpStatus.PRECONDITION_FAILED);
            }
        } catch (Exception e) {
            log.error("Exception occurred {0}", e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/verifyCode/{id}/{code}", method = RequestMethod.POST)
    @Operation(description = "This Service verifies code sent to email")
    public ResponseEntity<?> verify(@PathVariable String id, @PathVariable int code) {
        log.info("API Call To Verify Code");

        try {
            ResponseDTO response = loginService.verifyCode(id, code);
            if (response.getStatus().equals("SUCCESS")) {
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else if (response.getStatus().equals("EMPTY_TEXTFIELD")) {
                return new ResponseEntity<>(response, HttpStatus.PRECONDITION_REQUIRED);
            } else {
                return new ResponseEntity<>(response, HttpStatus.PRECONDITION_FAILED);
            }
        } catch (Exception e) {
            log.error("Exception occurred " + e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    @Operation(description = "This Service initiates a User login")
    public ResponseEntity<?> doLogin(@RequestBody AccessDTO accessDTO) {
        log.info("API Call To Login");

        try {
            LoginResponseDTO response = loginService.login(accessDTO);
            if (response.getStatus().equals("SUCCESS")) {
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else if (response.getStatus().equals("EMPTY_TEXTFIELD")) {
                return new ResponseEntity<>(response, HttpStatus.PRECONDITION_REQUIRED);
            } else {
                return new ResponseEntity<>(response, HttpStatus.PRECONDITION_FAILED);
            }
        } catch (Exception e) {
            log.error("Exception occurred " + e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
