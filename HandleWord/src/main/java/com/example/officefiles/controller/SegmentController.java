package com.example.officefiles.controller;

import com.example.officefiles.service.SegmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SegmentController {

    private final SegmentService segmentService;

    public SegmentController(SegmentService segmentService) {
        this.segmentService = segmentService;
    }

    @GetMapping("/write/{documentId}")
    public ResponseEntity<?> getByDocument(@PathVariable Long documentId) throws Exception {
        segmentService.writeDocx(documentId);
        return ResponseEntity.ok().body("success");
    }

}
