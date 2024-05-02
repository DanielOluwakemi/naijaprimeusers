package com.app.naijaprimeusers.restControllers;

import com.app.naijaprimeusers.dtos.DownloadMediaDTO;
import com.app.naijaprimeusers.dtos.ResponseDTO;
import com.app.naijaprimeusers.dtos.UploadMediaDTO;
import com.app.naijaprimeusers.services.MediaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@CrossOrigin(origins = "*")
@Tag(name = "Media Endpoints", description = "These endpoints manages media on naijaprimeusers")
@RequestMapping(path = "/media")
@RestController("mediaController")
public class MediaController {

    @Autowired
    private MediaService mediaService;

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    @Operation(description = "This Service uploads media on naijaprimeusers")
    public ResponseEntity<?> add(@RequestBody UploadMediaDTO mediaDTO, @RequestParam(name = "file", required = false) MultipartFile multipartFile,
                                 @RequestParam(name = "key", required = false) String key, @RequestParam(name = "type", required = false) String type){
        log.info("API Call To Upload Media");

        try {
            mediaDTO.setMultipartFile(multipartFile);
            if (key != null && !key.isBlank()) mediaDTO.setKey(key);
            if (type != null && !type.isBlank()) mediaDTO.setType(type);
            ResponseDTO response = mediaService.saveLargeFiles(mediaDTO);
            if(response.getStatus().equalsIgnoreCase("SUCCESS")) {
                return new ResponseEntity<>(response, HttpStatus.CREATED);
            }else if(response.getStatus().equalsIgnoreCase("EMPTY_TEXTFIELD")) {
                return new ResponseEntity<>(response, HttpStatus.PRECONDITION_REQUIRED);
            }else {
                return new ResponseEntity<>(response, HttpStatus.PRECONDITION_FAILED);
            }
        }catch(Exception e) {
            log.error("Exception occurred "+e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/uploadFile", method = RequestMethod.POST)
    @Operation(description = "This Service uploads frontend media on naijaprimeusers")
    public ResponseEntity<?> uploadFile(@RequestParam(name = "file", required = false) MultipartFile multipartFile,
                                        @RequestParam(name = "key", required = false) String key, @RequestParam(name = "type", required = false) String type){
        log.info("API Call To Upload Media");

        try {
            UploadMediaDTO mediaDTO = new UploadMediaDTO();
            mediaDTO.setMultipartFile(multipartFile);
            if (key != null && !key.isBlank()) mediaDTO.setKey(key);
            if (type != null && !type.isBlank()) mediaDTO.setType(type);
            ResponseDTO response = mediaService.saveLargeFiles(mediaDTO);
            if(response.getStatus().equalsIgnoreCase("SUCCESS")) {
                return new ResponseEntity<>(response, HttpStatus.CREATED);
            }else if(response.getStatus().equalsIgnoreCase("EMPTY_TEXTFIELD")) {
                return new ResponseEntity<>(response, HttpStatus.PRECONDITION_REQUIRED);
            }else {
                return new ResponseEntity<>(response, HttpStatus.PRECONDITION_FAILED);
            }
        }catch(Exception e) {
            log.error("Exception occurred "+e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/uploadSmallFiles", method = RequestMethod.POST)
    @Operation(description = "This Service uploads media on naijaprimeusers")
    public ResponseEntity<?> uploadSmallFiles(@RequestBody UploadMediaDTO mediaDTO, @RequestParam(name = "file", required = false) MultipartFile multipartFile,
                                              @RequestParam(name = "key", required = false) String key, @RequestParam(name = "type", required = false) String type){
        log.info("API Call To Upload Media");

        try {
            mediaDTO.setMultipartFile(multipartFile);
            if (key != null && !key.isBlank()) mediaDTO.setKey(key);
            if (type != null && !type.isBlank()) mediaDTO.setType(type);
            ResponseDTO response = mediaService.save(mediaDTO);
            if(response.getStatus().equalsIgnoreCase("SUCCESS")) {
                return new ResponseEntity<>(response, HttpStatus.CREATED);
            }else if(response.getStatus().equalsIgnoreCase("EMPTY_TEXTFIELD")) {
                return new ResponseEntity<>(response, HttpStatus.PRECONDITION_REQUIRED);
            }else {
                return new ResponseEntity<>(response, HttpStatus.PRECONDITION_FAILED);
            }
        }catch(Exception e) {
            log.error("Exception occurred "+e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/download", method = RequestMethod.POST)
    @Operation(description = "This Service downloads media on naijaprimeusers")
    public ResponseEntity<?> add(@RequestBody DownloadMediaDTO mediaDTO){
        log.info("API Call To Download Media");

        try {
            return ResponseEntity
                    .ok()
                    .cacheControl(CacheControl.noCache())
                    .header("Content-type", "application/octet-stream")
                    .header("Content-disposition", "attachment; filename=\"" + mediaDTO.getName() + "\"")
                    .body(new InputStreamResource(mediaService.find(mediaDTO.getName())));
        }catch(Exception e) {
            log.error("Exception occurred "+e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

//    @RequestMapping(value = "/gets/{jourID}", method = RequestMethod.GET)
//    @Operation(description = "This Service gets Journalist media")
//    public ResponseEntity<List<?>> gets(@PathVariable String jourID){
//        log.info("API Call To Fetch Journalist Media");
//
//        try {
//            return new ResponseEntity<>(mediaService.gets(jourID), HttpStatus.OK);
//        } catch (Exception e) {
//            log.error("Exception occurred " + e);
//            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }

    @RequestMapping(value = "/getByIds/{ids}", method = RequestMethod.GET)
    @Operation(description = "This Service get media")
    public ResponseEntity<List<?>> get(@PathVariable List<String> ids){
        log.info("API Call To Fetch Media");

        try {
            return new ResponseEntity<>(mediaService.getByIds(ids), HttpStatus.OK);
        } catch (Exception e) {
            log.error("Exception occurred " + e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/getByKey/{key}", method = RequestMethod.GET)
    @Operation(description = "This Service gets media by key")
    public ResponseEntity<?> getByKey(@PathVariable String key){
        log.info("API Call To Fetch Media By Key");

        try {
            return new ResponseEntity<>(mediaService.getByKey(key), HttpStatus.OK);
        } catch (Exception e) {
            log.error("Exception occurred " + e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/getS3Urls/{fileNames}", method = RequestMethod.GET)
    @Operation(description = "This Service get media S3 Url")
    public ResponseEntity<List<?>> getS3Urls(@PathVariable List<String> fileNames){
        log.info("API Call To Fetch Media S3 Url");

        try {
            return new ResponseEntity<>(mediaService.getS3Urls(fileNames), HttpStatus.OK);
        } catch (Exception e) {
            log.error("Exception occurred " + e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/delete/{key}", method = RequestMethod.DELETE)
    @Operation(description = "This Service deletes a media on naijaprimeusers")
    public ResponseEntity<?> delete(@PathVariable String key){
        log.info("API Call To Delete A Media");

        try {
            ResponseDTO response = new ResponseDTO();
            int retValue = mediaService.delete(key);
            if(retValue == 1) {
                response.setStatus("SUCCESS");
                response.setMessage("Deleted Media Successfully");
                return new ResponseEntity<>(response, HttpStatus.OK);
            }else if(retValue == 2) {
                response.setStatus("RECORD_NONEXISTS");
                response.setMessage("Record Does Not Exist");
                return new ResponseEntity<>(response, HttpStatus.PRECONDITION_FAILED);
            }else {
                response.setStatus("FAILURE");
                response.setMessage("Deleting Media Failed");
                return new ResponseEntity<>(response, HttpStatus.PRECONDITION_FAILED);
            }
        }catch(Exception e) {
            log.error("Exception occurred {0}",e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
