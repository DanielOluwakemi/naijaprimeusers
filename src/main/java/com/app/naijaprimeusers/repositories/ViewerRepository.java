package com.app.naijaprimeusers.repositories;

import com.app.naijaprimeusers.entities.Viewer;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ViewerRepository extends MongoRepository<Viewer, String> {
    Viewer findByUsernameAndDeleteFlag(String username, int deleteFlag);

    List<Viewer> findByIdIn(List<String> ids);
}
