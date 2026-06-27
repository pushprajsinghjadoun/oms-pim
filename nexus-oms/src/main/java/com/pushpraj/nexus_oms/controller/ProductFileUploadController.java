package com.pushpraj.nexus_oms.controller;

import com.pushpraj.nexus_oms.entity.FileImportLog;
import com.pushpraj.nexus_oms.service.ProductFileUploadService;
import com.pushpraj.nexus_oms.service.ProductImportProcessorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/products/upload")
public class ProductFileUploadController {

    private final ProductFileUploadService fileUploadService;
    private final ProductImportProcessorService processorService;

    @Autowired
    public ProductFileUploadController(ProductFileUploadService fileUploadService,
                                       ProductImportProcessorService processorService) {
        this.fileUploadService = fileUploadService;
        this.processorService = processorService;
    }

    @PostMapping
    public ResponseEntity<?> uploadProductFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("File is empty. Please select a valid file.");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !originalFilename.toLowerCase().endsWith(".json")) {
            return ResponseEntity.badRequest().body("Invalid file format. Only .json files are allowed.");
        }

        try {
            // Save file and log as QUEUED
            FileImportLog log = fileUploadService.uploadFile(file);
            
            // Trigger asynchronous processing immediately
            processorService.processFileAsync(log.getId());
            
            // Return 202 Accepted, indicating processing has started in the background
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(log);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to upload the file: " + e.getMessage());
        }
    }
}
