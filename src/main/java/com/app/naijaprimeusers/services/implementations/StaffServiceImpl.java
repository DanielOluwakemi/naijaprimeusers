package com.app.naijaprimeusers.services.implementations;

import com.app.naijaprimeusers.dtos.ResponseDTO;
import com.app.naijaprimeusers.entities.ContentCreator;
import com.app.naijaprimeusers.entities.Staff;
import com.app.naijaprimeusers.entities.Viewer;
import com.app.naijaprimeusers.repositories.ContentCreatorRepository;
import com.app.naijaprimeusers.repositories.StaffRepository;
import com.app.naijaprimeusers.repositories.ViewerRepository;
import com.app.naijaprimeusers.services.StaffService;
import com.app.naijaprimeusers.utils.DateConverter;
import com.app.naijaprimeusers.utils.EmailValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
public class StaffServiceImpl implements StaffService {

    @Autowired
    StaffRepository staffRepository;
    @Autowired
    ContentCreatorRepository contentCreatorRepository;
    @Autowired
    ViewerRepository viewerRepository;
    @Autowired
    EmailValidator emailValidator;
    @Autowired
    DateConverter dateConverter;

    @Override
    public ResponseDTO add(Staff staff) {
        log.info("Adding Staff");
        ResponseDTO response = new ResponseDTO();

        //Validation
        if(staff.getEmail() == null || staff.getEmail().isBlank() || staff.getUsername() == null || staff.getUsername().isBlank() ||
                staff.getFullName() == null || staff.getFullName().isBlank() || staff.getAge() == null || staff.getAge().isBlank()) {
            response.setStatus("EMPTY_TEXTFIELD");
            response.setMessage("Fill Empty Textfield(s)");
            return response;
        }
        if(!emailValidator.validate(staff.getEmail())) {
            response.setStatus("EMAIL_INVALID");
            response.setMessage("Email Is Invalid!");
            return response;
        }

        try {
            Staff staff1 = staffRepository.findByUsernameAndDeleteFlag(staff.getUsername(), 0);
            Viewer viewer = viewerRepository.findByUsernameAndDeleteFlag(staff.getUsername(), 0);
            if(staff1 != null || viewer != null) {
                response.setStatus("ACCOUNT_EXIST");
                response.setMessage("Account Already Exists For This Username!");
                return response;
            }

            staff.setCreatedTime(dateConverter.getCurrentTimestamp());
            staff.setDeleteFlag(0);
            staffRepository.save(staff);

            response.setStatus("SUCCESS");
            response.setMessage("Added Staff Successfully");
            response.setData(staffRepository.findByUsernameAndDeleteFlag(staff.getEmail(), 0));
            return response;
        }catch(Exception e) {
            log.error("Error While Adding Staff "+e);
            response.setStatus("FAILURE");
            response.setMessage("Adding Staff Failed");
            return response;
        }
    }

    @Override
    public ResponseDTO update(Staff staff) {
        log.info("Updating staff");
        ResponseDTO response = new ResponseDTO();

        //Validation
        if(staff.getEmail() == null || staff.getEmail().isBlank() || staff.getUsername() == null || staff.getUsername().isBlank() ||
                staff.getFullName() == null || staff.getFullName().isBlank() || staff.getAge() == null || staff.getAge().isBlank() ||
                staff.getId() == null || staff.getId().isBlank()) {
            response.setStatus("EMPTY_TEXTFIELD");
            response.setMessage("Fill Empty Textfield(s)");
            return response;
        }
        if(!emailValidator.validate(staff.getEmail())) {
            response.setStatus("EMAIL_INVALID");
            response.setMessage("Email Is Invalid!");
            return response;
        }

        try {
            Staff staff1 = staffRepository.findByUsernameAndDeleteFlag(staff.getEmail(), 0);
            if(staff1 == null) {
                response.setStatus("ACCOUNT_NONEXIST");
                response.setMessage("Account Does Not Exist!");
                return response;
            }
            if(!Objects.equals(staff1.getId(), staff.getId())) {
                response.setStatus("ACCOUNT_DISCREPANCY");
                response.setMessage("Account Has Some Discrepancy!");
                return response;
            }

            staffRepository.save(staff);
            response.setStatus("SUCCESS");
            response.setMessage("Updated staff Successfully");
            response.setData(staffRepository.findByUsernameAndDeleteFlag(staff.getEmail(), 0));
            return response;
        }catch(Exception e) {
            log.error("Error While Updating staff "+e);
            response.setStatus("FAILURE");
            response.setMessage("Updating staff Failed");
            return response;
        }
    }

    @Override
    public List<Staff> getByIds(List<String> ids) {
        return staffRepository.findByIdIn(ids);
    }

    @Override
    public List<Staff> getAll() {
        return staffRepository.findAll();
    }

    @Override
    public int delete(String id) {
        log.info("Deleting Staff");

        try {
            Optional<Staff> staff = staffRepository.findById(id);
            if (staff.isEmpty()) return 2;

            staff.get().setDeleteFlag(1);
            staffRepository.save(staff.get());
            return 1;
        } catch (Exception e) {
            log.error("Error While Deleting Staff {0}", e);
            return 0;
        }
    }
}
