package com.app.naijaprimeusers.services;

import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.app.naijaprimeusers.dtos.MediaDTO;
import com.app.naijaprimeusers.dtos.ResponseDTO;
import com.app.naijaprimeusers.dtos.UploadMediaDTO;
import com.app.naijaprimeusers.entities.Media;

import java.util.List;

public interface MediaService {

    ResponseDTO save(UploadMediaDTO mediaDTO);
    ResponseDTO saveLargeFiles(UploadMediaDTO mediaDTO);
//    List<Media> gets(String creatorID);
    List<Media> getByIds(List<String> ids);
    Media getByKey(String key);
    S3ObjectInputStream find(String fileName);
    List<String> getS3Urls(List<String> fileNames);
    List<MediaDTO> getS3UrlsByKey(List<String> keys);
    int delete(String key);
}
