package com.app.naijaprimeusers.services.implementations;

import com.app.naijaprimeusers.dtos.*;
import com.app.naijaprimeusers.entities.ContentCreator;
import com.app.naijaprimeusers.entities.Login;
import com.app.naijaprimeusers.entities.Staff;
import com.app.naijaprimeusers.entities.Viewer;
import com.app.naijaprimeusers.environment.APIs;
import com.app.naijaprimeusers.environment.Constants;
import com.app.naijaprimeusers.repositories.ContentCreatorRepository;
import com.app.naijaprimeusers.repositories.LoginRepository;
import com.app.naijaprimeusers.repositories.StaffRepository;
import com.app.naijaprimeusers.repositories.ViewerRepository;
import com.app.naijaprimeusers.services.GoMailerService;
import com.app.naijaprimeusers.services.LoginService;
import com.app.naijaprimeusers.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    StaffRepository staffRepository;
    @Autowired
    EmailValidator emailValidator;
    @Autowired
    PasswordValidator passwordValidator;
    @Autowired
    DateConverter dateConverter;
    @Autowired
    HashPassword hashPassword;
    @Autowired
    APIs apis;
    @Autowired
    GoMailerService goMailerService;
    @Autowired
    CodeGenerator codeGenerator;
    @Autowired
    MessageTemplates template;

    @Override
    public ResponseDTO add(Login login) {
        log.info("Adding Login");
        ResponseDTO response = new ResponseDTO();

        //Validation
        if(login.getUsername() == null || login.getUsername().isBlank() || login.getPassword() == null || login.getPassword().isBlank()) {
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
            login.setCodeCreatedTime(dateConverter.getCurrentTimestamp());
            login.setCode(sixDigitCode);
            login.setVerified(false);
            login.setDeleteFlag(0);
            if(!passwordValidator.validate(login.getPassword())){
                response.setStatus("PASSWORD_INVALID");
                response.setMessage("Password Must Fulfill Password Policy!");
                return response;
            }
            login.setPassword(hashPassword.hashPass(login.getPassword()));
            loginRepository.save(login);

            response.setStatus("SUCCESS");
            response.setMessage("Added Login Successfully");
            response.setData(loginRepository.findByUsernameIgnoreCaseAndDeleteFlag(login.getUsername(), 0));

            Staff staff = staffRepository.findByEmailAndDeleteFlag(login.getUsername(), 0);
            ContentCreator creator = contentCreatorRepository.findByEmailAndDeleteFlag(login.getUsername(), 0);
            Viewer viewer = viewerRepository.findByEmailAndDeleteFlag(login.getUsername(), 0);
            String fullName;
            if (staff != null) {
                fullName = staff.getFullName();
            } else if (creator != null) {
                fullName = creator.getFullName();
            } else {
                fullName = viewer.getFullName();
            }

            //Sending Email
            MessageDTO messageDTO = new MessageDTO();
            messageDTO.setFullName(fullName);
            messageDTO.setCode(sixDigitCode);

            MailDTO mailDTO = new MailDTO();
            mailDTO.setMsg(template.getTemplate(1, messageDTO));
            mailDTO.setSubject("Naija Prime registration - Verify Email");
            mailDTO.setTo(login.getUsername());
            mailDTO.setFrom(apis.getNoReply());
            mailDTO.setReceiverName(login.getUsername());

            goMailerService.sendEmail(mailDTO);

            response.setStatus("SUCCESS");
            response.setMessage("Added Login Successfully");
            response.setData(loginRepository.findByUsernameIgnoreCaseAndDeleteFlag(login.getUsername(), 0));

            return response;
        }catch(Exception e) {
            log.error("Error While Adding Login " + e);
            response.setStatus("FAILURE");
            response.setMessage("Adding Login Failed");
            return response;
        }
    }

    @Override
    public ResponseDTO verifyCode(String id, int code) {
        log.info("Verifying six digit code");
        ResponseDTO response = new ResponseDTO();

        if(id == null || id.isBlank() || code < 0) {
            response.setStatus("EMPTY_TEXTFIELD");
            response.setMessage("Fill Empty Textfield(s)");
            return response;
        }
        Optional<Login> login = loginRepository.findById(id);
        if (login.isEmpty()) {
            response.setStatus("LOGIN_NONEXISTS");
            response.setMessage("Login doesn't exist");
            return response;
        }
//        long timestamp = login.get().getCodeCreatedTime();
//        long expirationTime = timestamp + ((long) Constants.CODE_DURATION * 60 * 1000);
//        if (dateConverter.getCurrentTimestamp() >= expirationTime) {
//            response.setStatus("CODE_EXPIRED");
//            response.setStatus("Code has expired. Generate a new code");
//            return response;
//        }

        if (login.get().getCode() != code) {
            response.setStatus("INCORRECT_CODE");
            response.setStatus("Code is incorrect!");
            return response;
        } else {
            login.get().setVerified(true);
        }
        Login dbLogin = loginRepository.save(login.get());

        response.setStatus("SUCCESS");
        response.setMessage("Verified Email Successfully");
        response.setData(dbLogin);
        return response;
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
            Staff staff = staffRepository.findByEmailAndDeleteFlag(accessDTO.getUsername(), 0);
            ContentCreator creator = contentCreatorRepository.findByEmailAndDeleteFlag(accessDTO.getUsername(), 0);
            Viewer viewer = viewerRepository.findByEmailAndDeleteFlag(accessDTO.getUsername(), 0);
            if((login == null || staff == null) && (login == null || creator == null) && (login == null || viewer == null)) {
                response.setStatus("ACCOUNT_NONEXISTS");
                response.setMessage("User Account Does Not Exist!");
                return response;
            }
            if (!login.isVerified()) {
                response.setStatus("ACCOUNT_NOT_VERIFIED");
                response.setMessage("Check your email for the code to verify your email!");
                return response;
            }

            //Check if password is correct
            if(Boolean.FALSE.equals(hashPassword.validatePass(accessDTO.getPassword(), login.getPassword()))) {
                response.setStatus("INVALID_CREDENTIALS");
                response.setMessage("Username or Password Incorrect!");
                return response;
            }


            login.setCreatedTime(dateConverter.getCurrentTimestamp());
            loginRepository.save(login);

            response.setStatus("SUCCESS");
            response.setMessage("Login Successful");

            if (staff != null){
                response.setUserType(2);
                response.setData(staffRepository.findByEmailAndDeleteFlag(login.getUsername(), 0));
            }
            else if (creator != null){
                response.setUserType(1);
                response.setData(contentCreatorRepository.findByEmailAndDeleteFlag(login.getUsername(), 0));
            } else {
                response.setUserType(0);
                response.setData(viewerRepository.findByEmailAndDeleteFlag(login.getUsername(), 0));
            }

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
