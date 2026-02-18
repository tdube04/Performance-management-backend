package com.innovation.workplan.CollectionModels;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * WorkplanTemplate - Archives workplan templates for historical reference and auditing
 * This model stores the complete configuration of a quarter's workplan template
 * including performance areas, programs, weights, and audit trail information.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "workplan_templates_tbl")
@JsonInclude(JsonInclude.Include.NON_NULL)

public class WorkplanTemplate {
    @Transient
    public static final String SEQUENCE_NAME = "workplan_template_sequence";
    
    @Id
    Long id;
    
    // Template identification
    String templateName;
    String evaluationPeriod; // e.g., "2026-Q1"
    int year;
    String quarter; // e.g., "Q1", "Q2", "Q3", "Q4"
    
    // Template status
    String templateStatus; // "ACTIVE", "ARCHIVED", "DRAFT"
    
    // Performance areas with weights
    ArrayList<TemplatePerformanceArea> performanceAreas;
    
    // Audit trail
    LocalDateTime createdAt;
    LocalDateTime archivedAt;
    String createdBy;
    String archivedBy;
    String archiveReason;
    
    // Metadata
    int totalPerformanceAreas;
    double totalWeights;
    String version; // Template version for tracking
    
    /**
     * Nested class for Performance Area in template
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class TemplatePerformanceArea {
        String performanceArea;
        String section;
        String description;
        int weight;
        ArrayList<TemplateProgram> programs;
        Pillars pillars;
    }
    
    /**
     * Nested class for Program in template
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class TemplateProgram {
        String programName;
        double weight;
        String contributedPillar;
    }
}
