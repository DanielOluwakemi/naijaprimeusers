package com.app.naijaprimeusers.restControllers;

import com.app.naijaprimeusers.dtos.ResponseDTO;
import com.app.naijaprimeusers.entities.Staff;
import com.app.naijaprimeusers.services.StaffService;
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
@Tag(name = "Staff Endpoints", description = "These endpoints manages staffs on naijaprimeusers")
@RequestMapping(path = "/staffs")
@RestController("StaffController")
public class StaffController {
    @Autowired
    private StaffService staffService;

    @RequestMapping(value = "/add", method = RequestMethod.POST, produces="application/json", consumes="application/json")
    @Operation(description = "This Service creates a new staff on naijaprimeusers")
    public ResponseEntity<?> add(@RequestBody Staff staff){
        log.info("API Call To Add New Staff");

        try {
            ResponseDTO response = staffService.add(staff);
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
    @Operation(description = "This Service edits a staff on naijaprimeusers")
    public ResponseEntity<?> update(@RequestBody Staff staff){
        log.info("API Call To Edit Staff");

        try {
            ResponseDTO response = staffService.update(staff);
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

    @RequestMapping(value = "/get/{ids}", method = RequestMethod.GET)
    @Operation(description = "This Service gets staff by ids on naijaprimeusers")
    public ResponseEntity<List<?>> get(@PathVariable List<String> ids){
        log.info("API Call To Fetch Staff");

        try {
            return new ResponseEntity<>(staffService.getByIds(ids), HttpStatus.OK);
        } catch (Exception e) {
            log.error("Exception occurred " + e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/getAll", method = RequestMethod.GET)
    @Operation(description = "This Service gets all staffs on naijaprimeusers")
    public ResponseEntity<List<?>> getAll(){
        log.info("API Call To Fetch All Viewers");

        try {
            return new ResponseEntity<>(staffService.getAll(), HttpStatus.OK);
        } catch (Exception e) {
            log.error("Exception occurred " + e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    @Operation(description = "This Service deletes a staff on naijaprimeusers")
    public ResponseEntity<?> delete(@PathVariable String id){
        log.info("API Call To Delete A Staff");

        try {
            ResponseDTO response = new ResponseDTO();
            int retValue = staffService.delete(id);
            if(retValue == 1) {
                response.setStatus("SUCCESS");
                response.setMessage("Deleted Staff Successfully");
                return new ResponseEntity<>(response, HttpStatus.OK);
            }else if(retValue == 2) {
                response.setStatus("ACCOUNT_NONEXIST");
                response.setMessage("Staff Account Does Not Exist!");
                return new ResponseEntity<>(response, HttpStatus.PRECONDITION_FAILED);
            }else {
                response.setStatus("FAILURE");
                response.setMessage("Deleting Staff Failed");
                return new ResponseEntity<>(response, HttpStatus.PRECONDITION_FAILED);
            }
        }catch(Exception e) {
            log.error("Exception occured "+e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
