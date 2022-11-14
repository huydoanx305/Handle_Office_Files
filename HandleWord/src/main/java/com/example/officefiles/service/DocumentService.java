package com.example.officefiles.service;

import com.example.officefiles.entity.Document;
import org.docx4j.jaxb.XPathBinderAssociationIsPartialException;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.springframework.web.multipart.MultipartFile;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface DocumentService {

    List<Document> getAllDocument();

    // Upload file and save data
    Document save(MultipartFile file) throws Exception;

    Map<String, Object> previewDocument(Long documentId) throws Exception;

}
