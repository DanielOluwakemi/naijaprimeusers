package com.app.naijaprimeusers.services.implementations;

import com.app.naijaprimeusers.dtos.ResponseDTO;
import com.app.naijaprimeusers.dtos.UserSettingsDTO;
import com.app.naijaprimeusers.entities.ContentCreator;
import com.app.naijaprimeusers.entities.UserSettings;
import com.app.naijaprimeusers.repositories.ContentCreatorRepository;
import com.app.naijaprimeusers.repositories.UserSettingsRepository;
import com.app.naijaprimeusers.services.UserSettingsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
public class UserSettingsServiceImpl implements UserSettingsService {

    @Autowired
    UserSettingsRepository userSettingsRepository;
    @Autowired
    ContentCreatorRepository contentCreatorRepository;

    @Override
    public ResponseDTO add(UserSettings userSettings) {
        log.info("Adding User Settings");
        ResponseDTO response = new ResponseDTO();

        //Validation
        if(userSettings.getUserID() == null || userSettings.getUserID().isBlank() || userSettings.getTheme() == null || userSettings.getTheme().isBlank() ||
                userSettings.getPalette() == null || userSettings.getPalette().isBlank() || userSettings.getIsolationMade() == null || userSettings.getIsolationMade().isBlank()
        ) {
            response.setStatus("EMPTY_TEXTFIELD");
            response.setMessage("Fill Empty Textfield(s)");
            return response;
        }

        try {
            UserSettings settings = userSettingsRepository.findByUserID(userSettings.getUserID(), 0);
            if(settings != null) {
                response.setStatus("ACCOUNT_EXIST");
                response.setMessage("Settings already exists for this user");
                response.setData(settings);
                return response;
            }

            userSettings.setDeleteFlag(0);
            userSettingsRepository.save(userSettings);

            response.setStatus("SUCCESS");
            response.setMessage("Added User Settings Successfully");
            response.setData(userSettingsRepository.findByUserID(userSettings.getUserID(), 0));
            return response;
        }catch(Exception e) {
            log.error("Error While Adding User Settings "+e);
            response.setStatus("FAILURE");
            response.setMessage("Adding User Settings Failed");
            return response;
        }
    }

    @Override
    public ResponseDTO update(UserSettings userSettings) {
        log.info("Updating Content creator");
        ResponseDTO response = new ResponseDTO();

        //Validation
        //Validation
        if(userSettings.getUserID() == null || userSettings.getUserID().isBlank() || userSettings.getTheme() == null || userSettings.getTheme().isBlank() ||
                userSettings.getPalette() == null || userSettings.getPalette().isBlank() || userSettings.getIsolationMade() == null || userSettings.getIsolationMade().isBlank()
        ) {
            response.setStatus("EMPTY_TEXTFIELD");
            response.setMessage("Fill Empty Textfield(s)");
            return response;
        }

        try {
            UserSettings settings = userSettingsRepository.findByUserID(userSettings.getUserID(), 0);
            if(settings == null) {
                response.setStatus("ACCOUNT_NONEXIST");
                response.setMessage("Settings Does Not Exist!");
                return response;
            }
            if(!Objects.equals(userSettings.getId(), settings.getId())) {
                response.setStatus("SETTINGS_DISCREPANCY");
                response.setMessage("Settings Has Some Discrepancy!");
                return response;
            }

            userSettingsRepository.save(userSettings);
            response.setStatus("SUCCESS");
            response.setMessage("Updated User Settings Successfully");
            response.setData(userSettingsRepository.findByUserID(userSettings.getUserID(), 0));
            return response;
        }catch(Exception e) {
            log.error("Error While Updating User Settings "+e);
            response.setStatus("FAILURE");
            response.setMessage("Updating User Settings Failed");
            return response;
        }
    }

    @Override
    public UserSettingsDTO get(String userID) {
        log.info("getting article by jourID");
        UserSettingsDTO userSettingsDTOS = new UserSettingsDTO();
        UserSettings settings = userSettingsRepository.findByUserID(userID, 0);

        String userFullName = "";
        String userProdName = "";
        String paymentDetailsName = "";
        String acctNo = "";
        String bankName = "";
        Optional<ContentCreator> creator = contentCreatorRepository.findById(userID);
        if (creator.isEmpty()){
            userFullName = creator.get().getFullName();
            userProdName = creator.get().getProdName();
            paymentDetailsName = creator.get().getAccountDetails().getFullName();
            acctNo = creator.get().getAccountDetails().getAcctNo();
            bankName = creator.get().getAccountDetails().getBankDetails();
        }

        userSettingsDTOS.setId(settings.getId());
        userSettingsDTOS.setUserID(settings.getUserID());
        userSettingsDTOS.setTheme(settings.getTheme());
        userSettingsDTOS.setPalette(settings.getPalette());
        userSettingsDTOS.setUserFullName(userFullName);
        userSettingsDTOS.setUserProdName(userProdName);
        userSettingsDTOS.getSecurity().setIsolationMade(settings.getIsolationMade());
        userSettingsDTOS.getSecurity().setSignOut(settings.getSignOut());
        userSettingsDTOS.getPaymentDetails().setFullName(paymentDetailsName);
        userSettingsDTOS.getPaymentDetails().setAcctNo(acctNo);
        userSettingsDTOS.getPaymentDetails().setBankName(bankName);
        userSettingsDTOS.setDeleteFlag(settings.getDeleteFlag());
        return userSettingsDTOS;
    }
}
