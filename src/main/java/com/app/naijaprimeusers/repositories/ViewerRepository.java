package com.app.naijaprimeusers.repositories;

import com.app.naijaprimeusers.entities.Viewer;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ViewerRepository extends MongoRepository<Viewer, String> {
    Viewer findByEmailAndDeleteFlag(String email, int deleteFlag);

    List<Viewer> findByIdIn(List<String> ids);
}
