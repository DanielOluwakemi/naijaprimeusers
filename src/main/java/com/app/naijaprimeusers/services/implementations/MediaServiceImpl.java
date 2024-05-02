package com.app.naijaprimeusers.services.implementations;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.amazonaws.services.s3.transfer.Upload;
import com.app.naijaprimeusers.dtos.MediaDTO;
import com.app.naijaprimeusers.dtos.ResponseDTO;
import com.app.naijaprimeusers.dtos.UploadMediaDTO;
import com.app.naijaprimeusers.entities.Media;
import com.app.naijaprimeusers.environment.APIs;
import com.app.naijaprimeusers.repositories.MediaRepository;
import com.app.naijaprimeusers.services.MediaService;
import com.app.naijaprimeusers.utils.DateConverter;
import com.app.naijaprimeusers.utils.FileConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class MediaServiceImpl implements MediaService {

    @Autowired
    MediaRepository mediaRepository;
    @Autowired
    AmazonS3 amazonS3;
    @Autowired
    APIs apis;
    @Autowired
    DateConverter dateConverter;
    @Autowired
    FileConverter fileConverter;

    @Override
    public ResponseDTO save(UploadMediaDTO mediaDTO) {
        log.info("Uploading Media Document");
        ResponseDTO response = new ResponseDTO();

        //Validation
        if(mediaDTO.getKey() == null || mediaDTO.getKey().isBlank() || mediaDTO.getType() == null || mediaDTO.getType().isBlank()){
            response.setStatus("EMPTY_TEXTFIELD");
            response.setMessage("Fill Empty Textfield(s)");
            return response;
        }

        try{
            Media media = mediaRepository.findByKeyAndDeleteFlag(mediaDTO.getKey(), 0);
            if(media != null){
                //Update this feature to just remove and save the new one in the future
                response.setStatus("RECORD_EXISTS");
                response.setMessage("Record Already Exist");
                return response;
            }

            //Checking file size
//            if (mediaDTO.getMultipartFile() != null && mediaDTO.getMultipartFile().getSize() > apis.getMaxFileSize()){
//                response.setStatus("MAX_SIZE_EXCEEDED");
//                response.setMessage("File Maximum Size Exceeded!");
//                return response;
//            }

            File file;
            if(mediaDTO.getMultipartFile() != null) file = fileConverter.convertMultiPartFileToFile(mediaDTO.getMultipartFile());
            else {
                if(mediaDTO.getFile() != null) file = mediaDTO.getFile();
                else{
                    response.setStatus("FILE_NOT_PRESENT");
                    response.setMessage("There Is No File Present In This Request!");
                    return response;
                }
            }

            String displayName = file.getName();
            long suffix = new Date().getTime();
            final String fileName = file.getName();
            log.info("Uploading file with name {}", suffix);

            final PutObjectRequest putObjectRequest = new PutObjectRequest(apis.getS3BucketName(), fileName, file);
            amazonS3.putObject(putObjectRequest);
            Files.delete(file.toPath()); // Remove the file locally created in the project folder

            //Saving to database
            Media newMedia = new Media();
            newMedia.setDeleteFlag(0);
            newMedia.setCreatedTime(dateConverter.getCurrentTimestamp());
            newMedia.setDisplayName(displayName);
            newMedia.setKey(mediaDTO.getKey());
            newMedia.setName(fileName);
            newMedia.setType(mediaDTO.getType());
            mediaRepository.save(newMedia);

            response.setStatus("SUCCESS");
            response.setMessage("Saved Media Successfully");
            response.setData(mediaRepository.findByKeyAndDeleteFlag(mediaDTO.getKey(), 0));
            return response;
        }catch (AmazonServiceException e) {
            log.error("Error {} occurred while uploading file", e.getLocalizedMessage());
        }catch (IOException ex) {
            log.error("Error {} occurred while deleting temporary file", ex.getLocalizedMessage());
        }catch (Exception e){
            log.error("Error While Saving File {0} " , e);
        }

        response.setStatus("FAILURE");
        response.setMessage("Saving Media Failed");
        return response;
    }

    @Override
    public ResponseDTO saveLargeFiles(UploadMediaDTO mediaDTO) {
        log.info("Uploading Large Media Document");
        ResponseDTO response = new ResponseDTO();

        //Validation
        if(mediaDTO.getKey() == null || mediaDTO.getKey().isBlank() || mediaDTO.getType() == null || mediaDTO.getType().isBlank()){
            response.setStatus("EMPTY_TEXTFIELD");
            response.setMessage("Fill Empty Textfield(s)");
            return response;
        }

        try{
            Media media = mediaRepository.findByKeyAndDeleteFlag(mediaDTO.getKey(), 0);
            if(media != null){
                //Update this feature to just remove and save the new one in the future
                response.setStatus("RECORD_EXISTS");
                response.setMessage("Record Already Exist");
                return response;
            }

            //Checking file size
//            if (mediaDTO.getMultipartFile() != null && mediaDTO.getMultipartFile().getSize() > apis.getMaxFileSize()){
//                response.setStatus("MAX_SIZE_EXCEEDED");
//                response.setMessage("File Maximum Size Exceeded!");
//                return response;
//            }

            File file;
            if(mediaDTO.getMultipartFile() != null) file = fileConverter.convertMultiPartFileToFile(mediaDTO.getMultipartFile());
            else {
                if(mediaDTO.getFile() != null) file = mediaDTO.getFile();
                else{
                    response.setStatus("FILE_NOT_PRESENT");
                    response.setMessage("There Is No File Present In This Request!");
                    return response;
                }
            }

            String displayName = file.getName();
            long suffix = new Date().getTime();
            final String fileName = file.getName();
            log.info("Uploading file with name {}", suffix);

            TransferManager tm = TransferManagerBuilder.standard().withS3Client(amazonS3)
                    .withMultipartUploadThreshold((long) (50 * 1024 * 1025)).build();

            long start = System.currentTimeMillis();
            Upload result = tm.upload(apis.getS3BucketName(), file.getName(), file);
            result.waitForCompletion();
            long end = System.currentTimeMillis();
            log.info("Complete Multipart Uploading {}s", (end - start) / 1000);

            //Saving to database
            Media newMedia = new Media();
            newMedia.setDeleteFlag(0);
            newMedia.setCreatedTime(dateConverter.getCurrentTimestamp());
            newMedia.setDisplayName(displayName);
            newMedia.setKey(mediaDTO.getKey());
            newMedia.setName(fileName);
            newMedia.setType(mediaDTO.getType());
            mediaRepository.save(newMedia);

            response.setStatus("SUCCESS");
            response.setMessage("Saved Media Successfully");
            response.setData(mediaRepository.findByKeyAndDeleteFlag(mediaDTO.getKey(), 0));
            return response;
        }catch (AmazonServiceException e) {
            log.error("Error {} occurred while uploading file", e.getLocalizedMessage());
        }catch (Exception e){
            log.error("Error While Saving File {0} " , e);
        }

        response.setStatus("FAILURE");
        response.setMessage("Saving Media Failed");
        return response;
    }

//    @Override
//    public List<Media> gets(String creatorID) {
//        log.info("Getting All Media");
//
//        return mediaRepository.findByCreatorIDAndDeleteFlag(creatorID, 0);
//    }

    @Override
    public List<Media> getByIds(List<String> ids) {
        log.info("Getting Media By ID");

        return mediaRepository.findByIdIn(ids);
    }

    @Override
    public Media getByKey(String key) {
        log.info("Getting Media By Key");

        return mediaRepository.findByKeyAndDeleteFlag(key, 0);
    }

    @Override
    public S3ObjectInputStream find(String fileName) {
        log.info("Downloading file");

        return amazonS3.getObject(apis.getS3BucketName(), fileName).getObjectContent();
    }

    @Override
    public List<String> getS3Urls(List<String> fileNames) {
        log.info("Getting file URLs");

        List<String> urlList = new ArrayList<>();
        List<Media> media = mediaRepository.findByNameIn(fileNames);
        if (media.size() <= 0) return urlList;

        for (Media myMedia : media){
            urlList.add(amazonS3.getUrl(apis.getS3BucketName(), myMedia.getName()).toExternalForm());
        }

        return urlList;
    }

    @Override
    public List<MediaDTO> getS3UrlsByKey(List<String> keys) {
        log.info("Getting file URLs");

        List<MediaDTO> urlList = new ArrayList<>();
        List<Media> media = mediaRepository.findByKeyIn(keys);
        if (media.size() <= 0) return urlList;

        for (Media myMedia : media){
            MediaDTO mediaDTO = new MediaDTO();
            mediaDTO.setFileName(amazonS3.getUrl(apis.getS3BucketName(), myMedia.getName()).toExternalForm());
            mediaDTO.setKey(myMedia.getKey());
            urlList.add(mediaDTO);
        }

        return urlList;
    }

    @Override
    public int delete(String key) {
        log.info("Deleting Media By Key");

        try{
            Media media = mediaRepository.findByKeyAndDeleteFlag(key, 0);
            if (media == null) return 2;

            log.info("Permissions {} " , amazonS3.getBucketPolicy(apis.getS3BucketName()).getPolicyText());
            amazonS3.deleteObject(apis.getS3BucketName(), media.getName());
            media.setDeleteFlag(1);
            mediaRepository.save(media);
            return 1;
        }catch (Exception e){
            log.error("Error While Deleting Media By Key {0}" , e);
            return 0;
        }
    }
}
