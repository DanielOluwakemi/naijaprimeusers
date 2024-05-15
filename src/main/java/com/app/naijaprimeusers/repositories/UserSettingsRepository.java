package com.app.naijaprimeusers.repositories;

import com.app.naijaprimeusers.entities.UserSettings;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserSettingsRepository extends MongoRepository<UserSettings, String> {
    UserSettings findByUserID(String userID, int deleteFlag);
}
