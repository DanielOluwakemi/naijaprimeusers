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

        try {
            Login log = loginRepository.findByUsernameIgnoreCaseAndUserTypeAndDeleteFlag(login.getUsername(), login.getUserType(), 0);
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

            Staff staff = staffRepository.findByUsernameAndDeleteFlag(login.getUsername(), 0);
            ContentCreator creator = contentCreatorRepository.findByEmailAndDeleteFlag(login.getUsername(), 0);
            Viewer viewer = viewerRepository.findByUsernameAndDeleteFlag(login.getUsername(), 0);
            String fullName = null;
            String email = null;
            if (staff != null) {
                fullName = staff.getFullName();
                email = staff.getEmail();
            } else if (creator != null) {
                fullName = creator.getFullName();
                email = creator.getEmail();
            } else if (viewer != null){
                fullName = viewer.getFullName();
                email = viewer.getEmail();
            } else {
                response.setStatus("EMAIL_NONEXISTS");
                response.setMessage("Account has not been created for this email");
                return response;
            }

            //Sending Email
            MessageDTO messageDTO = new MessageDTO();
            messageDTO.setFullName(fullName);
            messageDTO.setCode(sixDigitCode);

            MailDTO mailDTO = new MailDTO();
            mailDTO.setMsg(template.getTemplate(1, messageDTO));
            mailDTO.setSubject("Naija Prime registration - Verify Email");
            mailDTO.setTo(email);
            mailDTO.setFrom(apis.getNoReply());
            mailDTO.setReceiverName(email);

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

        if (id == null || id.isBlank() || code <= 0) {
            response.setStatus("EMPTY_TEXTFIELD");
            response.setMessage("Fill Empty Textfield(s)");
            return response;
        }
        try {
        Optional<Login> login = loginRepository.findById(id);
        if (login.isEmpty()) {
            response.setStatus("LOGIN_NONEXISTS");
            response.setMessage("Login doesn't exist");
            return response;
        }
        long timestamp = login.get().getCodeCreatedTime();
        long expirationTime = timestamp + ((long) Constants.CODE_DURATION * 60 * 1000);
        if (dateConverter.getCurrentTimestamp() >= expirationTime) {
            response.setStatus("CODE_EXPIRED");
            response.setStatus("Code has expired. Generate a new code");
            return response;
        }

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

      }catch(Exception e) {
            log.error("Error While Verifying Email " + e);
            response.setStatus("FAILURE");
            response.setMessage("Verifying Email Failed");
            return response;
        }
    }

    @Override
    public ResponseDTO resendCode(String id) {
        log.info("Resending Code");
        ResponseDTO response = new ResponseDTO();

        if(id == null || id.isBlank()) {
            response.setStatus("EMPTY_TEXTFIELD");
            response.setMessage("Fill Empty Textfield(s)");
            return response;
        }

        try {
            Optional<Login> login = loginRepository.findById(id);
            if (login.isEmpty()) {
                response.setStatus("LOGIN_NONEXISTS");
                response.setMessage("Login doesn't exist");
                return response;
            }
            if (login.get().isVerified()) {
                response.setStatus("ACCOUNT_ALREADY_VERIFIED");
                response.setMessage("Account has already been verified");
                return response;
            }
            Login account = login.get();
            int sixDigitCode = codeGenerator.generateRandomSixDigitCode();
            account.setCodeCreatedTime(dateConverter.getCurrentTimestamp());
            account.setCode(sixDigitCode);
            account.setVerified(false);

            Staff staff = staffRepository.findByUsernameAndDeleteFlag(account.getUsername(), 0);
            ContentCreator creator = contentCreatorRepository.findByEmailAndDeleteFlag(account.getUsername(), 0);
            Viewer viewer = viewerRepository.findByUsernameAndDeleteFlag(account.getUsername(), 0);
            String fullName;
            String email;
            if (staff != null) {
                fullName = staff.getFullName();
                email = staff.getEmail();
            } else if (creator != null) {
                fullName = creator.getFullName();
                email = creator.getEmail();
            } else {
                fullName = viewer.getFullName();
                email = viewer.getEmail();
            }

            //Sending Email
            MessageDTO messageDTO = new MessageDTO();
            messageDTO.setFullName(fullName);
            messageDTO.setCode(sixDigitCode);

            MailDTO mailDTO = new MailDTO();
            mailDTO.setMsg(template.getTemplate(1, messageDTO));
            mailDTO.setSubject("Naija Prime registration - Verify Email");
            mailDTO.setTo(email);
            mailDTO.setFrom(apis.getNoReply());
            mailDTO.setReceiverName(email);

            goMailerService.sendEmail(mailDTO);

            response.setStatus("SUCCESS");
            response.setMessage("Resent Code Successfully");
            response.setData(loginRepository.findByUsernameIgnoreCaseAndDeleteFlag(account.getUsername(), 0));
            return response;
        } catch(Exception e) {
            log.error("Error While Resending Code " + e);
            response.setStatus("FAILURE");
            response.setMessage("Resending Code Failed");
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
            Login login = loginRepository.findByUsernameIgnoreCaseAndUserTypeAndDeleteFlag(accessDTO.getUsername(), accessDTO.getUserType(), 0);
            Staff staff = staffRepository.findByUsernameAndDeleteFlag(accessDTO.getUsername(), 0);
            ContentCreator creator = contentCreatorRepository.findByEmailAndDeleteFlag(accessDTO.getUsername(), 0);
            Viewer viewer = viewerRepository.findByUsernameAndDeleteFlag(accessDTO.getUsername(), 0);
            if((login == null || staff == null) && (login == null || creator == null) && (login == null || viewer == null)) {
                response.setStatus("ACCOUNT_NONEXISTS");
                response.setMessage("User Account Does Not Exist!");
                return response;
            }
            if (login.getUserType() == 1) {
                Login creatorlogin = loginRepository.findByUsernameIgnoreCaseAndUserTypeAndDeleteFlag(accessDTO.getUsername(), 1, 0);
                if (creatorlogin == null || creator == null) {
                    response.setStatus("CONTENT_CREATOR_ACCOUNT_NONEXISTS");
                    response.setMessage("Content Creator Account Does Not Exist!");
                    return response;
                }
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

            if (accessDTO.getUserType() == 2){
                response.setUserType(2);
                response.setData(staffRepository.findByUsernameAndDeleteFlag(login.getUsername(), 0));
            }
            else if (accessDTO.getUserType() == 1){
                response.setUserType(1);
                response.setData(contentCreatorRepository.findByEmailAndDeleteFlag(login.getUsername(), 0));
            } else {
                response.setUserType(0);
                response.setData(viewerRepository.findByUsernameAndDeleteFlag(login.getUsername(), 0));
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

    @Override
    public ResponseDTO updateEmail(ResetEmailDTO resetEmailDTO) {
        log.info("Updating Username");
        ResponseDTO response = new ResponseDTO();

        //Validation
        if(resetEmailDTO.getEmail() == null || resetEmailDTO.getEmail().isBlank() || resetEmailDTO.getNewEmail() == null || resetEmailDTO.getNewEmail().isBlank() ||
           resetEmailDTO.getUsername() == null || resetEmailDTO.getUsername().isBlank()) {
            response.setStatus("EMPTY_TEXTFIELD");
            response.setMessage("Fill Empty Textfield(s)");
            return response;
        }

        try {
            //Check if user exist
            Login account = loginRepository.findByUsernameIgnoreCaseAndDeleteFlag(resetEmailDTO.getUsername(), 0);
            Staff staff = staffRepository.findByUsernameAndDeleteFlag(resetEmailDTO.getUsername(), 0);
            ContentCreator creator = contentCreatorRepository.findByEmailAndDeleteFlag(resetEmailDTO.getUsername(), 0);
            Viewer viewer = viewerRepository.findByUsernameAndDeleteFlag(resetEmailDTO.getUsername(), 0);
            if((account == null || staff == null) && (account == null || creator == null) && (account == null || viewer == null)) {
                response.setStatus("ACCOUNT_NONEXISTS");
                response.setMessage("User Account Does Not Exist!");
                return response;
            }
            if (!account.isVerified()) {
                response.setStatus("ACCOUNT_NOT_VERIFIED");
                response.setMessage("Check your email for the code to verify your email!");
                return response;
            }
            if(!emailValidator.validate(resetEmailDTO.getNewEmail())) {
                response.setStatus("EMAIL_INVALID");
                response.setMessage("Email Is Invalid!");
                return response;
            }

            account.setUsername(resetEmailDTO.getNewEmail());
            String fullName;
            if (account.getUserType() == 0) {
                viewer.setEmail(resetEmailDTO.getNewEmail());
                fullName = viewer.getFullName();
            } else if (account.getUserType() == 1) {
                creator.setEmail(resetEmailDTO.getNewEmail());
                fullName = creator.getFullName();
            } else {
                staff.setEmail(resetEmailDTO.getNewEmail());
                fullName = staff.getFullName();
            }
            account.setCreatedTime(dateConverter.getCurrentTimestamp());

            int sixDigitCode = codeGenerator.generateRandomSixDigitCode();
            account.setCodeCreatedTime(dateConverter.getCurrentTimestamp());
            account.setCode(sixDigitCode);
            account.setVerified(false);
            loginRepository.save(account);

            //Sending Email
            MessageDTO messageDTO = new MessageDTO();
            messageDTO.setFullName(fullName);
            messageDTO.setCode(sixDigitCode);

            MailDTO mailDTO = new MailDTO();
            mailDTO.setMsg(template.getTemplate(2, messageDTO));
            mailDTO.setSubject("Naija Prime Reset Email - Verify New Email");
            mailDTO.setTo(resetEmailDTO.getNewEmail());
            mailDTO.setFrom(apis.getNoReply());
            mailDTO.setReceiverName(resetEmailDTO.getNewEmail());

            goMailerService.sendEmail(mailDTO);

            response.setStatus("SUCCESS");
            response.setMessage("Reset Email Successfully, Check Your New Email To Verify it");

            return response;
        }catch(Exception e) {
            log.error("Error While Do Login " + e);
            response.setStatus("FAILURE");
            response.setMessage("Login Failed");
            return response;
        }
    }
}
