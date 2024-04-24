package com.app.naijaprimeusers.services.implementations;

import com.app.naijaprimeusers.dtos.AccessDTO;
import com.app.naijaprimeusers.dtos.LoginResponseDTO;
import com.app.naijaprimeusers.dtos.ResponseDTO;
import com.app.naijaprimeusers.entities.ContentCreator;
import com.app.naijaprimeusers.entities.Login;
import com.app.naijaprimeusers.entities.Viewer;
import com.app.naijaprimeusers.repositories.ContentCreatorRepository;
import com.app.naijaprimeusers.repositories.LoginRepository;
import com.app.naijaprimeusers.repositories.ViewerRepository;
import com.app.naijaprimeusers.services.LoginService;
import com.app.naijaprimeusers.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    LoginRepository loginRepository;
    @Autowired
    ContentCreatorRepository contentCreatorRepository;
    @Autowired
    ViewerRepository viewerRepository;
    @Autowired
    EmailValidator emailValidator;
    @Autowired
    PasswordValidator passwordValidator;
    @Autowired
    DateConverter dateConverter;
    @Autowired
    HashPassword hashPassword;
    @Autowired
    CodeGenerator codeGenerator;

    @Override
    public ResponseDTO add(Login login) {
        log.info("Adding Login");
        ResponseDTO response = new ResponseDTO();

        //Validation
        if(login.getUsername() == null || login.getUsername().isBlank()) {
            response.setStatus("EMPTY_TEXTFIELD");
            response.setMessage("Fill Empty Textfield(s)");
            return response;
        }
        if(!emailValidator.validate(login.getUsername())) {
            response.setStatus("EMAIL_INVALID");
            response.setMessage("Email Is Invalid!");
            return response;
        }

        try {
            Login log = loginRepository.findByUsernameIgnoreCaseAndDeleteFlag(login.getUsername(), 0);
            if(log != null) {
                response.setStatus("ACCOUNT_EXIST");
                response.setMessage("Login Account Already Exists!");
                return response;
            }

            int sixDigitCode = codeGenerator.generateRandomSixDigitCode();
            login.setCreatedTime(dateConverter.getCurrentTimestamp());
            login.setDeleteFlag(0);
//            if(login.getPassword() == null || login.getPassword().isBlank()) login.setPassword(hashPassword.hashPass(coupon));
//            else {
                if(!passwordValidator.validate(login.getPassword())){
                    response.setStatus("PASSWORD_INVALID");
                    response.setMessage("Password Must Fulfill Password Policy!");
                    return response;
                }
                login.setPassword(hashPassword.hashPass(login.getPassword()));
//            }
            loginRepository.save(login);

            response.setStatus("SUCCESS");
            response.setMessage("Added Login Successfully");
            response.setData(loginRepository.findByUsernameIgnoreCaseAndDeleteFlag(login.getUsername(), 0));

//            //Sending Email
//            MessageDTO messageDTO = new MessageDTO();
//            messageDTO.setDummyPassword(coupon);
//            //messageDTO.setLoginURL(APIs.getFrontend());
//            MailDTO mailDTO = new MailDTO();
//            mailDTO.setMsg(template.getTemplate(1, messageDTO));
//            mailDTO.setSubject("PlutoSpace - User Registration");
//            mailDTO.setTo(login.getUsername());
//            //MimeMessage message = sender.createEmail(mailDTO);
//            //log.info("Email Status " + sender.sendMessage(service, "me", message));

            return response;
        }catch(Exception e) {
            log.error("Error While Adding Login " + e);
            response.setStatus("FAILURE");
            response.setMessage("Adding Login Failed");
            return response;
        }
    }

    @Override
    public LoginResponseDTO login(AccessDTO accessDTO) {
        log.info("Do Login");
        LoginResponseDTO response = new LoginResponseDTO();

        //Validation
        if(accessDTO.getPassword() == null || accessDTO.getPassword().isBlank() || accessDTO.getUsername() == null || accessDTO.getUsername().isBlank()) {
            response.setStatus("EMPTY_TEXTFIELD");
            response.setMessage("Fill Empty Textfield(s)");
            return response;
        }

        try {
            //Check if user exist
            Login login = loginRepository.findByUsernameIgnoreCaseAndDeleteFlag(accessDTO.getUsername(), 0);
            ContentCreator creator = contentCreatorRepository.findByEmailAndDeleteFlag(accessDTO.getUsername(), 0);
            Viewer viewer = viewerRepository.findByEmailAndDeleteFlag(accessDTO.getUsername(), 0);
            if(login == null || creator == null) {
                response.setStatus("ACCOUNT_NONEXISTS");
                response.setMessage("User Account Does Not Exist!");
                return response;
            }

            //Check if password is correct
            if(Boolean.FALSE.equals(hashPassword.validatePass(accessDTO.getPassword(), login.getPassword()))) {
                response.setStatus("INVALID_CREDENTIALS");
                response.setMessage("Username or Password Incorrect!");
                return response;
            }
            
            //Successful (Send Roles and Permissions And Other Details)
            String roleID = "0";
//            if(personalCompany.getRoleID() != null) roleID = personalCompany.getRoleID();
//            login.setToken(this.generateEncryptedLoginToken(login.getOrgID(), "" + login.getEmpID(), roleID));
            login.setCreatedTime(dateConverter.getCurrentTimestamp());
            loginRepository.save(login);

            response.setStatus("SUCCESS");
            response.setMessage("Login Successful");
//            response.setData(loginRepository.);

            //Checking for birthday wishesa
//            response.setWishBirthday(false);
//            int currentMonth = dateConverter.getCurrentMonth() + 1;
//            int currentDay = dateConverter.getCurrentDay();
//            if((creator.getDayOfBirth() == currentDay) && (creator.getMonthOfBirth() == currentMonth)){
//                List<LoginHistory> histories = loginHistoryRepository.findByOrgIDAndEmpIDAndLoginTimeGreaterThanEquals(login.getOrgID(), login.getEmpID(), dateConverter.getStartOfDayTimestamp());
//                if(histories.isEmpty()) response.setWishBirthday(true);
//            }

            //Adding Login History
//            LoginHistory history = new LoginHistory();
//            history.setLoginTime(dateConverter.getCurrentTimestamp());
//            history.setDevice(request.getHeader("User-Agent"));
//            history.setUserID(login.getEmpID());
//            history.setOrgID(login.getOrgID());
//            loginHistoryRepository.save(history);

//            headers.setHeader("token-1", login.getToken());
            return response;
        }catch(Exception e) {
            log.error("Error While Do Login " + e);
            response.setStatus("FAILURE");
            response.setMessage("Login Failed");
            return response;
        }
    }
}
