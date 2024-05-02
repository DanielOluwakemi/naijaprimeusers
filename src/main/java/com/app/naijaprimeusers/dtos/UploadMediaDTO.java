package com.app.naijaprimeusers.dtos;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;

@Data
public class UploadMediaDTO {

    private MultipartFile multipartFile;
    private File file;
    private String key;
    private String type;
}
