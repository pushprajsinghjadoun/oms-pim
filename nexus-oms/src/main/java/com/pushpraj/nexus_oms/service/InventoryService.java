package com.pushpraj.nexus_oms.service;

import com.pushpraj.nexus_oms.entity.Inventory;
import com.pushpraj.nexus_oms.repository.InventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class InventoryService {

    private final InventoryRepository repository;

    @Autowired
    public InventoryService(InventoryRepository repository) {
        this.repository = repository;
    }

    public List<Inventory> findAll() {
        return repository.findAll();
    }

    public Optional<Inventory> findById(UUID id) {
        return repository.findById(id);
    }

    public Inventory save(Inventory entity) {
        return repository.save(entity);
    }

    public void deleteById(UUID id) {
        repository.deleteById(id);
    }
}
