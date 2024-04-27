package com.app.naijaprimeusers.restControllers;

import com.app.naijaprimeusers.dtos.ResponseDTO;
import com.app.naijaprimeusers.entities.ContentCreator;
import com.app.naijaprimeusers.services.ContentCreatorService;
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
@Tag(name = "Content Creator Endpoints", description = "These endpoints manages content creators on naijaprimeusers")
@RequestMapping(path = "/contentCreators")
@RestController("ContentCreatorController")
public class ContentCreatorController {

    @Autowired
    private ContentCreatorService contentCreatorService;

    @RequestMapping(value = "/add", method = RequestMethod.POST, produces="application/json", consumes="application/json")
    @Operation(description = "This Service creates a new content creator on naijaprimeusers")
    public ResponseEntity<?> add(@RequestBody ContentCreator creator){
        log.info("API Call To Add New Content Creator");

        try {
            ResponseDTO response = contentCreatorService.add(creator);
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
    @Operation(description = "This Service edits a content creator on naijaprimeusers")
    public ResponseEntity<?> update(@RequestBody ContentCreator creator){
        log.info("API Call To Edit Content Creator");

        try {
            ResponseDTO response = contentCreatorService.update(creator);
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
    @Operation(description = "This Service gets a content creator by ids on naijaprimeusers")
    public ResponseEntity<List<?>> get(@PathVariable List<String> ids){
        log.info("API Call To Fetch Content Creator");

        try {
            return new ResponseEntity<>(contentCreatorService.getByIds(ids), HttpStatus.OK);
        } catch (Exception e) {
            log.error("Exception occurred " + e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/getAll", method = RequestMethod.GET)
    @Operation(description = "This Service gets all content creators on naijaprimeusers")
    public ResponseEntity<List<?>> getAll(){
        log.info("API Call To Fetch All Viewers");

        try {
            return new ResponseEntity<>(contentCreatorService.getAll(), HttpStatus.OK);
        } catch (Exception e) {
            log.error("Exception occurred " + e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    @Operation(description = "This Service deletes a content creator on naijaprimeusers")
    public ResponseEntity<?> delete(@PathVariable String id){
        log.info("API Call To Delete A Content Creator");

        try {
            ResponseDTO response = new ResponseDTO();
            int retValue = contentCreatorService.delete(id);
            if(retValue == 1) {
                response.setStatus("SUCCESS");
                response.setMessage("Deleted Content Creator Successfully");
                return new ResponseEntity<>(response, HttpStatus.OK);
            }else if(retValue == 2) {
                response.setStatus("ACCOUNT_NONEXIST");
                response.setMessage("Content Creator Account Does Not Exist!");
                return new ResponseEntity<>(response, HttpStatus.PRECONDITION_FAILED);
            }else {
                response.setStatus("FAILURE");
                response.setMessage("Deleting Content Creator Failed");
                return new ResponseEntity<>(response, HttpStatus.PRECONDITION_FAILED);
            }
        }catch(Exception e) {
            log.error("Exception occured "+e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
