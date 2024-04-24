package com.app.naijaprimeusers.services;

import com.app.naijaprimeusers.dtos.ResponseDTO;
import com.app.naijaprimeusers.entities.ContentCreator;

import java.util.List;

public interface ContentCreatorService {

    ResponseDTO add(ContentCreator creator);
    ResponseDTO update(ContentCreator creator);
    List<ContentCreator> getByIds(List<String> ids);
    List<ContentCreator> getAll();
    int delete(String id);
}
