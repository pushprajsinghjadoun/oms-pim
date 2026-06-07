package com.pushpraj.nexus_oms.controller;

import com.pushpraj.nexus_oms.entity.FileImportLog;
import com.pushpraj.nexus_oms.service.FileImportLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/file-import-logs")
public class FileImportLogController {

    private final FileImportLogService service;

    @Autowired
    public FileImportLogController(FileImportLogService service) {
        this.service = service;
    }

    @GetMapping
    public List<FileImportLog> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<FileImportLog> getById(@PathVariable UUID id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public FileImportLog create(@RequestBody FileImportLog entity) {
        return service.save(entity);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FileImportLog> update(@PathVariable UUID id, @RequestBody FileImportLog entity) {
        return service.findById(id).map(existing -> {
            // Updating existing record using save since ID is the same
            return ResponseEntity.ok(service.save(entity));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        if (service.findById(id).isPresent()) {
            service.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
