package com.app.naijaprimeusers.services.implementations;

import com.app.naijaprimeusers.dtos.ResponseDTO;
import com.app.naijaprimeusers.entities.ContentCreator;
import com.app.naijaprimeusers.entities.Staff;
import com.app.naijaprimeusers.entities.Viewer;
import com.app.naijaprimeusers.repositories.ContentCreatorRepository;
import com.app.naijaprimeusers.repositories.StaffRepository;
import com.app.naijaprimeusers.repositories.ViewerRepository;
import com.app.naijaprimeusers.services.ContentCreatorService;
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
public class ContentCreatorServiceImpl implements ContentCreatorService {
    
    @Autowired
    EmailValidator emailValidator;
    @Autowired
    ContentCreatorRepository contentCreatorRepository;
    @Autowired
    ViewerRepository viewerRepository;
    @Autowired
    StaffRepository staffRepository;
    @Autowired
    DateConverter dateConverter;
    
    @Override
    public ResponseDTO add(ContentCreator creator) {
        log.info("Adding Content Creator");
        ResponseDTO response = new ResponseDTO();

        //Validation
        if(creator.getEmail() == null || creator.getEmail().isBlank() || creator.getFullName() == null || creator.getFullName().isBlank() ||
                creator.getAge() == null || creator.getAge().isBlank() || creator.getProdName() == null || creator.getProdName().isBlank()
//              ||  creator.getPhoneNumber() == null || creator.getPhoneNumber().isBlank() ||
//            creator.getFilmProdLocation() == null || creator.getFilmProdLocation().isBlank() || creator.getMembership() == null || creator.getMembership().isBlank() ||
//            creator.getAccountDetails().getFullName() == null || creator.getAccountDetails().getFullName().isBlank() || creator.getAccountDetails().getAcctNo() == null || creator.getAccountDetails().getAcctNo().isBlank() ||
//            creator.getAccountDetails().getBankDetails() == null || creator.getAccountDetails().getBankDetails().isBlank() || creator.getFile().getImageID() == null || creator.getFile().getImageID().isBlank() ||
//            creator.getFile().getImageUrl() == null || creator.getFile().getImageUrl().isBlank()
        ) {
            response.setStatus("EMPTY_TEXTFIELD");
            response.setMessage("Fill Empty Textfield(s)");
            return response;
        }
        if(!emailValidator.validate(creator.getEmail())) {
            response.setStatus("EMAIL_INVALID");
            response.setMessage("Email Is Invalid!");
            return response;
        }

        try {
            ContentCreator contentCreator = contentCreatorRepository.findByEmailAndDeleteFlag(creator.getEmail(), 0);
            Viewer viewer = viewerRepository.findByUsernameAndDeleteFlag(creator.getProdName(), 0);
            if(contentCreator != null || viewer != null) {
                response.setStatus("ACCOUNT_EXIST");
                response.setMessage("Account Already Exists For This Email!");
                response.setData(contentCreator);
                return response;
            }

            creator.setCreatedTime(dateConverter.getCurrentTimestamp());
            creator.setDeleteFlag(0);
            contentCreatorRepository.save(creator);

            response.setStatus("SUCCESS");
            response.setMessage("Added Content creator Successfully");
            response.setData(contentCreatorRepository.findByEmailAndDeleteFlag(creator.getEmail(), 0));
            return response;
        }catch(Exception e) {
            log.error("Error While Adding Content creator "+e);
            response.setStatus("FAILURE");
            response.setMessage("Adding Content creator Failed");
            return response;
        }
    }

    @Override
    public ResponseDTO update(ContentCreator creator) {
        log.info("Updating Content creator");
        ResponseDTO response = new ResponseDTO();

        //Validation
        if(creator.getEmail() == null || creator.getEmail().isBlank() || creator.getPhoneNumber() == null || creator.getPhoneNumber().isBlank() ||
                creator.getFullName() == null || creator.getFullName().isBlank() || creator.getAge() == null || creator.getAge().isBlank() || creator.getProdName() == null || creator.getProdName().isBlank() ||
                creator.getFilmProdLocation() == null || creator.getFilmProdLocation().isBlank() || creator.getMembership() == null || creator.getMembership().isBlank() ||
                creator.getAccountDetails().getFullName() == null || creator.getAccountDetails().getFullName().isBlank() || creator.getAccountDetails().getAcctNo() == null || creator.getAccountDetails().getAcctNo().isBlank() ||
                creator.getAccountDetails().getBankDetails() == null || creator.getAccountDetails().getBankDetails().isBlank() || creator.getFile().getImageID() == null || creator.getFile().getImageID().isBlank() ||
                creator.getFile().getImageUrl() == null || creator.getFile().getImageUrl().isBlank() || creator.getId() == null || creator.getId().isBlank()
        ) {
            response.setStatus("EMPTY_TEXTFIELD");
            response.setMessage("Fill Empty Textfield(s)");
            return response;
        }
        if(!emailValidator.validate(creator.getEmail())) {
            response.setStatus("EMAIL_INVALID");
            response.setMessage("Email Is Invalid!");
            return response;
        }

        try {
            ContentCreator contentCreator = contentCreatorRepository.findByEmailAndDeleteFlag(creator.getEmail(), 0);
            Viewer viewer = viewerRepository.findByUsernameAndDeleteFlag(creator.getProdName(), 0);
            if(contentCreator == null) {
                response.setStatus("ACCOUNT_NONEXIST");
                response.setMessage("Account Does Not Exist!");
                return response;
            }
            if(!Objects.equals(contentCreator.getId(), creator.getId())) {
                response.setStatus("ACCOUNT_DISCREPANCY");
                response.setMessage("Account Has Some Discrepancy!");
                return response;
            }

            contentCreatorRepository.save(creator);
            response.setStatus("SUCCESS");
            response.setMessage("Updated Content creator Successfully");
            response.setData(contentCreatorRepository.findByProdNameAndDeleteFlag(creator.getProdName(), 0));
            return response;
        }catch(Exception e) {
            log.error("Error While Updating Content creator "+e);
            response.setStatus("FAILURE");
            response.setMessage("Updating Content creator Failed");
            return response;
        }
    }

    @Override
    public List<ContentCreator> getByIds(List<String> ids) {
        return contentCreatorRepository.findByIdIn(ids);
    }

    @Override
    public List<ContentCreator> getAll() {
        return contentCreatorRepository.findAll();
    }

    @Override
    public int delete(String id) {
        log.info("Deleting ContentCreator");

        try {
            Optional<ContentCreator> creator = contentCreatorRepository.findById(id);
            if (creator.isEmpty()) return 2;

            creator.get().setDeleteFlag(1);
            contentCreatorRepository.save(creator.get());
            return 1;
        } catch (Exception e) {
            log.error("Error While Deleting ContentCreator {0}", e);
            return 0;
        }
    }
}
