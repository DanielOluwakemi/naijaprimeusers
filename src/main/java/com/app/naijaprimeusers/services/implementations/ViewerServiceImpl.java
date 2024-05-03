package com.app.naijaprimeusers.services.implementations;

import com.app.naijaprimeusers.dtos.ResponseDTO;
import com.app.naijaprimeusers.entities.ContentCreator;
import com.app.naijaprimeusers.entities.Staff;
import com.app.naijaprimeusers.entities.Viewer;
import com.app.naijaprimeusers.repositories.ContentCreatorRepository;
import com.app.naijaprimeusers.repositories.StaffRepository;
import com.app.naijaprimeusers.repositories.ViewerRepository;
import com.app.naijaprimeusers.services.ViewerService;
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
public class ViewerServiceImpl implements ViewerService {
    
    @Autowired
    ViewerRepository viewerRepository;
    @Autowired
    StaffRepository staffRepository;
    @Autowired
    ContentCreatorRepository contentCreatorRepository;
    @Autowired
    EmailValidator emailValidator;
    @Autowired
    DateConverter dateConverter;
    
    @Override
    public ResponseDTO add(Viewer viewer) {
        log.info("Adding Viewer");
        ResponseDTO response = new ResponseDTO();

        //Validation
        if(viewer.getEmail() == null || viewer.getEmail().isBlank() || viewer.getUsername() == null || viewer.getUsername().isBlank() ||
                viewer.getFullName() == null || viewer.getFullName().isBlank() || viewer.getAge() == null || viewer.getAge().isBlank()) {
            response.setStatus("EMPTY_TEXTFIELD");
            response.setMessage("Fill Empty Textfield(s)");
            return response;
        }
        if(!emailValidator.validate(viewer.getEmail())) {
            response.setStatus("EMAIL_INVALID");
            response.setMessage("Email Is Invalid!");
            return response;
        }

        try {
            Viewer viewer1 = viewerRepository.findByUsernameAndDeleteFlag(viewer.getUsername(), 0);
            ContentCreator creator = contentCreatorRepository.findByProdNameAndDeleteFlag(viewer.getUsername(), 0);
            if(viewer1 != null || creator != null) {
                response.setStatus("ACCOUNT_EXIST");
                response.setMessage("Account Already Exists For this Username!");
                response.setData(viewer1);
                return response;
            }

            viewer.setCreatedTime(dateConverter.getCurrentTimestamp());
            viewer.setDeleteFlag(0);
            viewerRepository.save(viewer);

            response.setStatus("SUCCESS");
            response.setMessage("Added Viewer Successfully");
            response.setData(viewerRepository.findByUsernameAndDeleteFlag(viewer.getEmail(), 0));
            return response;
        }catch(Exception e) {
            log.error("Error While Adding Viewer "+e);
            response.setStatus("FAILURE");
            response.setMessage("Adding Viewer Failed");
            return response;
        }    }

    @Override
    public ResponseDTO update(Viewer viewer) {
        log.info("Updating Content viewer");
        ResponseDTO response = new ResponseDTO();

        //Validation
        if(viewer.getEmail() == null || viewer.getEmail().isBlank() || viewer.getUsername() == null || viewer.getUsername().isBlank() ||
                viewer.getFullName() == null || viewer.getFullName().isBlank() || viewer.getAge() == null || viewer.getAge().isBlank() ||
                viewer.getId() == null || viewer.getId().isBlank()) {
            response.setStatus("EMPTY_TEXTFIELD");
            response.setMessage("Fill Empty Textfield(s)");
            return response;
        }
        if(!emailValidator.validate(viewer.getEmail())) {
            response.setStatus("EMAIL_INVALID");
            response.setMessage("Email Is Invalid!");
            return response;
        }

        try {
            Viewer viewer1 = viewerRepository.findByUsernameAndDeleteFlag(viewer.getEmail(), 0);
            if(viewer1 == null) {
                response.setStatus("ACCOUNT_NONEXIST");
                response.setMessage("Account Does Not Exist!");
                return response;
            }
            if(!Objects.equals(viewer1.getId(), viewer.getId())) {
                response.setStatus("ACCOUNT_DISCREPANCY");
                response.setMessage("Account Has Some Discrepancy!");
                return response;
            }

            viewerRepository.save(viewer);
            response.setStatus("SUCCESS");
            response.setMessage("Updated Viewer Successfully");
            response.setData(viewerRepository.findByUsernameAndDeleteFlag(viewer.getEmail(), 0));
            return response;
        }catch(Exception e) {
            log.error("Error While Updating Viewer "+e);
            response.setStatus("FAILURE");
            response.setMessage("Updating Viewer Failed");
            return response;
        }    }

    @Override
    public List<Viewer> getByIds(List<String> ids) {
        return viewerRepository.findByIdIn(ids);
    }

    @Override
    public List<Viewer> getAll() {
        return viewerRepository.findAll();
    }

    @Override
    public int delete(String id) {
        log.info("Deleting Viewer");

        try {
            Optional<Viewer> viewer = viewerRepository.findById(id);
            if (viewer.isEmpty()) return 2;

            viewer.get().setDeleteFlag(1);
            viewerRepository.save(viewer.get());
            return 1;
        } catch (Exception e) {
            log.error("Error While Deleting Viewer {0}", e);
            return 0;
        }
    }
}
