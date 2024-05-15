package com.app.naijaprimeusers.restControllers;

import com.app.naijaprimeusers.dtos.ResponseDTO;
import com.app.naijaprimeusers.entities.UserSettings;
import com.app.naijaprimeusers.services.UserSettingsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@CrossOrigin(origins = "*")
@Tag(name = "User Settings Endpoints", description = "These endpoints manages user settings on naijaprimeusers")
@RequestMapping(path = "/userSettings")
@RestController("UserSettingsController")
public class UserSettingsController {

    @Autowired
    private UserSettingsService userSettingsService;

    @RequestMapping(value = "/add", method = RequestMethod.POST, produces="application/json", consumes="application/json")
    @Operation(description = "This Service creates a new User Settings on naijaprimeusers")
    public ResponseEntity<?> add(@RequestBody UserSettings settings){
        log.info("API Call To Add New User Settings");

        try {
            ResponseDTO response = userSettingsService.add(settings);
            if(response.getStatus().equalsIgnoreCase("SUCCESS")) {
                return new ResponseEntity<>(response, HttpStatus.CREATED);
            }else if(response.getStatus().equalsIgnoreCase("EMPTY_TEXTFIELD")) {
                return new ResponseEntity<>(response, HttpStatus.PRECONDITION_REQUIRED);
            }else {
                return new ResponseEntity<>(response, HttpStatus.PRECONDITION_FAILED);
            }
        }catch(Exception e) {
            log.error("Exception occured "+e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST, produces="application/json", consumes="application/json")
    @Operation(description = "This Service edits a User Settings on naijaprimeusers")
    public ResponseEntity<?> update(@RequestBody UserSettings creator){
        log.info("API Call To Edit User Settings");

        try {
            ResponseDTO response = userSettingsService.update(creator);
            if(response.getStatus().equalsIgnoreCase("SUCCESS")) {
                return new ResponseEntity<>(response, HttpStatus.OK);
            }else if(response.getStatus().equalsIgnoreCase("EMPTY_TEXTFIELD")) {
                return new ResponseEntity<>(response, HttpStatus.PRECONDITION_REQUIRED);
            }else {
                return new ResponseEntity<>(response, HttpStatus.PRECONDITION_FAILED);
            }
        }catch(Exception e) {
            log.error("Exception occured "+e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/get/{userID}", method = RequestMethod.GET)
    @Operation(description = "This Service gets a User Settings by ids on naijaprimeusers")
    public ResponseEntity<?> get(@PathVariable String userID){
        log.info("API Call To Fetch User Settings");

        try {
            return new ResponseEntity<>(userSettingsService.get(userID), HttpStatus.OK);
        } catch (Exception e) {
            log.error("Exception occurred " + e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
