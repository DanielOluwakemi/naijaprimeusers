package com.app.naijaprimeusers.repositories;

import com.app.naijaprimeusers.entities.Media;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MediaRepository extends MongoRepository<Media, String> {

    Media findByKeyAndDeleteFlag(String key, int deleteFlag);

    List<Media> findByDeleteFlag(int deleteFlag);

    List<Media> findByIdIn(List<String> ids);

    List<Media> findByNameIn(List<String> fileNames);

    Media findByNameAndDeleteFlag(String fileName, int deleteFlag);

    List<Media> findByKeyIn(List<String> keys);

}
