package com.app.naijaprimeusers.repositories;

import com.app.naijaprimeusers.entities.ContentCreator;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContentCreatorRepository extends MongoRepository<ContentCreator, String> {

    ContentCreator findByUsernameAndDeleteFlag(String username, int deleteFlag);

    List<ContentCreator> findByIdIn(List<String> ids);
}
