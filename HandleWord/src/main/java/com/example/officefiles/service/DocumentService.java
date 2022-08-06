package com.example.officefiles.service;

import com.example.officefiles.entity.Document;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface DocumentService {

    List<Document> getAllDocument();

    // Upload file and save data
    Document save(MultipartFile file) throws Exception;

}
