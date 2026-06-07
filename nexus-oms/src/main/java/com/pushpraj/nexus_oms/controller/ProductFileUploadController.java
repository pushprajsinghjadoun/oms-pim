package com.pushpraj.nexus_oms.controller;

import com.pushpraj.nexus_oms.entity.FileImportLog;
import com.pushpraj.nexus_oms.service.ProductFileUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/products/upload")
public class ProductFileUploadController {

    private final ProductFileUploadService fileUploadService;

    @Autowired
    public ProductFileUploadController(ProductFileUploadService fileUploadService) {
        this.fileUploadService = fileUploadService;
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
            FileImportLog log = fileUploadService.uploadFile(file);
            return ResponseEntity.status(HttpStatus.CREATED).body(log);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to upload the file: " + e.getMessage());
        }
    }
}
