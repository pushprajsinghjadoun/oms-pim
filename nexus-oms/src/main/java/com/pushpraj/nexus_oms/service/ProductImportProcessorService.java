package com.pushpraj.nexus_oms.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.pushpraj.nexus_oms.entity.Category;
import com.pushpraj.nexus_oms.entity.FileImportLog;
import com.pushpraj.nexus_oms.entity.Product;
import com.pushpraj.nexus_oms.entity.ProductAttribute;
import com.pushpraj.nexus_oms.repository.CategoryRepository;
import com.pushpraj.nexus_oms.repository.FileImportLogRepository;
import com.pushpraj.nexus_oms.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.UUID;

@Service
public class ProductImportProcessorService {

    private final FileImportLogRepository importLogRepository;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ObjectMapper objectMapper;

    @Autowired
    public ProductImportProcessorService(FileImportLogRepository importLogRepository,
                                         ProductRepository productRepository,
                                         CategoryRepository categoryRepository) {
        this.importLogRepository = importLogRepository;
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.objectMapper = new ObjectMapper();
    }

    @Async("fileImportExecutor")
    public void processFileAsync(UUID fileImportLogId) {
        FileImportLog importLog = importLogRepository.findById(fileImportLogId).orElse(null);
        if (importLog == null) return;

        importLog.setStatus("RUNNING");
        importLog = importLogRepository.save(importLog);

        File file = new File(importLog.getFilePath());
        if (!file.exists()) {
            importLog.setStatus("FAILED");
            importLog.setErrorReportPath("File not found on disk");
            importLogRepository.save(importLog);
            return;
        }

        try (JsonParser jsonParser = new JsonFactory().createParser(file)) {
            
            boolean foundProducts = false;
            while (!jsonParser.isClosed()) {
                JsonToken token = jsonParser.nextToken();
                if (token == null) break;
                
                if (JsonToken.FIELD_NAME.equals(token) && "products".equals(jsonParser.getCurrentName())) {
                    foundProducts = true;
                    jsonParser.nextToken(); // move to START_ARRAY
                    break;
                }
            }

            if (!foundProducts || jsonParser.currentToken() != JsonToken.START_ARRAY) {
                throw new IllegalArgumentException("Invalid JSON format: missing 'products' array");
            }

            int successCount = 0;
            int failedCount = 0;

            while (jsonParser.nextToken() == JsonToken.START_OBJECT) {
                try {
                    // Read exactly one product into memory
                    JsonNode pNode = objectMapper.readTree(jsonParser);
                    processSingleProduct(pNode);
                    successCount++;
                    
                    // Periodically update the log every 1000 records for real-time progress
                    if ((successCount + failedCount) % 1000 == 0) {
                        importLog.setSuccessfullyProcessed(successCount);
                        importLog.setFailedRecords(failedCount);
                        importLogRepository.save(importLog);
                    }
                } catch (Exception e) {
                    failedCount++;
                }
            }

            int totalRecords = successCount + failedCount;
            importLog.setTotalRecordsToProcess(totalRecords);
            importLog.setSuccessfullyProcessed(successCount);
            importLog.setFailedRecords(failedCount);
            
            if (totalRecords == 0) {
                importLog.setStatus("FAILED");
                importLog.setErrorReportPath("No products found to process");
            } else {
                importLog.setStatus(failedCount == 0 ? "COMPLETED" : "PARTIAL_SUCCESS");
            }
            
            importLogRepository.save(importLog);

        } catch (Exception e) {
            importLog.setStatus("FAILED");
            importLog.setErrorReportPath(e.getMessage());
            importLogRepository.save(importLog);
        }
    }

    private void processSingleProduct(JsonNode pNode) {
        String sku = pNode.path("sku").asText(null);
        if (sku == null || sku.trim().isEmpty()) {
            throw new IllegalArgumentException("SKU is missing");
        }

        // Upsert Product
        Product product = productRepository.findBySku(sku).orElse(new Product());
        product.setSku(sku);
        product.setTitle(pNode.path("title").asText(null));
        product.setBrand(pNode.path("brand").asText(null));
        product.setPartNumber(pNode.path("part_number").asText(null));
        product.setStatus(pNode.path("status").asText("ACTIVE"));

        // Process Category
        JsonNode categoryNode = pNode.path("category");
        if (!categoryNode.isMissingNode()) {
            String catSlug = categoryNode.path("slug").asText(null);
            if (catSlug != null && !catSlug.isEmpty()) {
                Category category = categoryRepository.findBySlug(catSlug).orElseGet(() -> {
                    Category newCat = new Category();
                    newCat.setSlug(catSlug);
                    newCat.setName(catSlug); // Default name to slug if not provided
                    return categoryRepository.save(newCat);
                });
                product.setCategory(category);
            }
        }

        // Initialize Attributes list if new
        if (product.getAttributes() == null) {
            product.setAttributes(new ArrayList<>());
        } else {
            product.getAttributes().clear(); // Clear existing attributes for this upsert
        }

        JsonNode attrsNode = pNode.path("attributes");
        if (attrsNode.isArray()) {
            for (JsonNode attrNode : attrsNode) {
                ProductAttribute attr = new ProductAttribute();
                attr.setProduct(product);
                attr.setType(attrNode.path("type").asText(null));
                attr.setSubType(attrNode.path("sub_type").asText(null));
                attr.setTitle(attrNode.path("title").asText(null));
                product.getAttributes().add(attr);
            }
        }

        productRepository.save(product);
    }
}
