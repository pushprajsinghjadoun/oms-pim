package com.pushpraj.nexus_oms.service;

import com.pushpraj.nexus_oms.entity.FileImportLog;
import com.pushpraj.nexus_oms.repository.FileImportLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class ProductFileUploadService {

    private final FileImportLogRepository fileImportLogRepository;
    
    @Value("${nexus.oms.file-upload-dir}")
    private String uploadDir;

    @Autowired
    public ProductFileUploadService(FileImportLogRepository fileImportLogRepository) {
        this.fileImportLogRepository = fileImportLogRepository;
    }

    public FileImportLog uploadFile(MultipartFile file) throws IOException {
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename() != null ? file.getOriginalFilename() : "unknown_file");
        
        String timestamp = String.valueOf(System.currentTimeMillis());
        String uniqueFileName;
        int dotIndex = originalFileName.lastIndexOf('.');
        if (dotIndex != -1) {
            uniqueFileName = originalFileName.substring(0, dotIndex) + "_" + timestamp + originalFileName.substring(dotIndex);
        } else {
            uniqueFileName = originalFileName + "_" + timestamp;
        }
        Path filePath = uploadPath.resolve(uniqueFileName);
        
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        String importType = "UNKNOWN";
        if (originalFileName.toLowerCase().endsWith(".json")) {
            importType = "PRODUCT_JSON";
        }

        FileImportLog fileImportLog = FileImportLog.builder()
                .originalFileName(originalFileName)
                .filePath(filePath.toAbsolutePath().toString())
                .importType(importType)
                .status("PENDING")
                .totalRecordsToProcess(0)
                .successfullyProcessed(0)
                .failedRecords(0)
                .build();

        return fileImportLogRepository.save(fileImportLog);
    }
}
