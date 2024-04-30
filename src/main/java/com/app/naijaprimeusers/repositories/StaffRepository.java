package com.app.naijaprimeusers.repositories;

import com.app.naijaprimeusers.entities.Staff;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StaffRepository extends MongoRepository<Staff, String> {

    Staff findByUsernameAndDeleteFlag(String username, int deleteFlag);

    List<Staff> findByIdIn(List<String> ids);
}
