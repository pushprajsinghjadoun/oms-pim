package com.pushpraj.nexus_oms.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "file_import_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileImportLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String originalFileName;
    private String filePath;
    private String importType;
    private String status;
    private Long springBatchJobId;
    private Integer totalRecordsToProcess;
    private Integer successfullyProcessed;
    private Integer failedRecords;
    private String errorReportPath;

    @CreationTimestamp
    @Column(name = "created_date", updatable = false)
    private LocalDateTime createdDate;

    @UpdateTimestamp
    @Column(name = "last_updated_date")
    private LocalDateTime lastUpdatedDate;
}
