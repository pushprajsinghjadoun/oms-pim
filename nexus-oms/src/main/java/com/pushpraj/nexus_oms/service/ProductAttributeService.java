package com.pushpraj.nexus_oms.service;

import com.pushpraj.nexus_oms.entity.ProductAttribute;
import com.pushpraj.nexus_oms.repository.ProductAttributeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProductAttributeService {

    private final ProductAttributeRepository repository;

    @Autowired
    public ProductAttributeService(ProductAttributeRepository repository) {
        this.repository = repository;
    }

    public List<ProductAttribute> findAll() {
        return repository.findAll();
    }

    public Optional<ProductAttribute> findById(UUID id) {
        return repository.findById(id);
    }

    public ProductAttribute save(ProductAttribute entity) {
        return repository.save(entity);
    }

    public void deleteById(UUID id) {
        repository.deleteById(id);
    }
}
