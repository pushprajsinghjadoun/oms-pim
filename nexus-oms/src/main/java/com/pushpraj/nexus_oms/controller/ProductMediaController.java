package com.pushpraj.nexus_oms.controller;

import com.pushpraj.nexus_oms.entity.ProductMedia;
import com.pushpraj.nexus_oms.service.ProductMediaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/productMedias")
public class ProductMediaController {

    private final ProductMediaService service;

    @Autowired
    public ProductMediaController(ProductMediaService service) {
        this.service = service;
    }

    @GetMapping
    public List<ProductMedia> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductMedia> getById(@PathVariable UUID id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ProductMedia create(@RequestBody ProductMedia entity) {
        return service.save(entity);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductMedia> update(@PathVariable UUID id, @RequestBody ProductMedia entity) {
        return service.findById(id).map(existing -> {
            // Assuming the client passed the correct ID in the path
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
