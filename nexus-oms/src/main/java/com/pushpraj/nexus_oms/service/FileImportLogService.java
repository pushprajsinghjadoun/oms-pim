package com.pushpraj.nexus_oms.service;

import com.pushpraj.nexus_oms.entity.FileImportLog;
import com.pushpraj.nexus_oms.repository.FileImportLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class FileImportLogService {

    private final FileImportLogRepository repository;

    @Autowired
    public FileImportLogService(FileImportLogRepository repository) {
        this.repository = repository;
    }

    public List<FileImportLog> findAll() {
        return repository.findAll();
    }

    public Optional<FileImportLog> findById(UUID id) {
        return repository.findById(id);
    }

    public FileImportLog save(FileImportLog entity) {
        return repository.save(entity);
    }

    public void deleteById(UUID id) {
        repository.deleteById(id);
    }
}
