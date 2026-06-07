package com.pushpraj.nexus_oms.service;

import com.pushpraj.nexus_oms.entity.ProductMedia;
import com.pushpraj.nexus_oms.repository.ProductMediaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProductMediaService {

    private final ProductMediaRepository repository;

    @Autowired
    public ProductMediaService(ProductMediaRepository repository) {
        this.repository = repository;
    }

    public List<ProductMedia> findAll() {
        return repository.findAll();
    }

    public Optional<ProductMedia> findById(UUID id) {
        return repository.findById(id);
    }

    public ProductMedia save(ProductMedia entity) {
        return repository.save(entity);
    }

    public void deleteById(UUID id) {
        repository.deleteById(id);
    }
}
