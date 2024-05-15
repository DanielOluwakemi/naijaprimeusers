package com.app.naijaprimeusers.services;

import com.app.naijaprimeusers.dtos.ResponseDTO;
import com.app.naijaprimeusers.dtos.UserSettingsDTO;
import com.app.naijaprimeusers.entities.UserSettings;

public interface UserSettingsService {

    ResponseDTO add(UserSettings userSettings);
    ResponseDTO update(UserSettings userSettings);
    UserSettingsDTO get(String userID);
}
