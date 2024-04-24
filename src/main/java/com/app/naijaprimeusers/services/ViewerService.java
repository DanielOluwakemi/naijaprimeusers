package com.app.naijaprimeusers.services;

import com.app.naijaprimeusers.dtos.ResponseDTO;
import com.app.naijaprimeusers.entities.Viewer;

import java.util.List;

public interface ViewerService {

    ResponseDTO add(Viewer viewer);
    ResponseDTO update(Viewer viewer);
    List<Viewer> getByIds(List<String>ids);
    List<Viewer> getAll();
    int delete(String id);
}
