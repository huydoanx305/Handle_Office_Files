package com.example.officefiles.service;

import com.example.officefiles.entity.Document;

import java.util.List;

public interface SegmentService {
    // Write Data from DB to file docx
    void writeDocx(Long documentId) throws Exception;

    // Save Paragraph and Run
    void saveSegment(List<Object> jaxbNodes, Document document) throws Exception;
}
