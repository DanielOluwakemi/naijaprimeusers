package com.app.naijaprimeusers.repositories;

import com.app.naijaprimeusers.entities.Login;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface LoginRepository extends MongoRepository<Login, String> {

    Login findByUsernameIgnoreCaseAndDeleteFlag(String username, int deleteFlag);
}
