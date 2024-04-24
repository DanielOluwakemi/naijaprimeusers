package com.app.naijaprimeusers.services;

import com.app.naijaprimeusers.dtos.ResponseDTO;
import com.app.naijaprimeusers.entities.Staff;

import java.util.List;

public interface StaffService {

    ResponseDTO add(Staff staff);
    ResponseDTO update(Staff staff);
    List<Staff> getByIds(List<String>ids);
    List<Staff> getAll();
    int delete(String id);
}
