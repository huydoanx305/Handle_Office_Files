package com.example.officefiles.controller;

import com.example.officefiles.service.DocumentService;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXBException;
import java.util.Base64;

@RestController
@RequestMapping("/api")
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @GetMapping("/documents")
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok().body(documentService.getAllDocument());
    }

    @GetMapping("/preview/{id}")
    public ResponseEntity<?> previewDocument(@PathVariable Long id) throws Exception {
        return ResponseEntity.ok().body(documentService.previewDocument(id));
    }

    @PostMapping("/upload")
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file) throws Exception {
        return ResponseEntity.ok().body(documentService.save(file));
    }
}
