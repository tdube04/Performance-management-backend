package com.innovation.workplan.Services;

import com.innovation.workplan.CollectionModels.WorkplanTemplate;

import java.util.List;

public interface WorkplanTemplateService {
    
    // Template archival operations
    WorkplanTemplate archiveCurrentTemplate(String evaluationPeriod, String archivedBy, String archiveReason);
    
    // Template retrieval operations
    List<WorkplanTemplate> getAllArchivedTemplates();
    
    List<WorkplanTemplate> getTemplatesByYear(int year);
    
    WorkplanTemplate getTemplateByEvaluationPeriod(String evaluationPeriod);
    
    WorkplanTemplate getActiveTemplate();
    
    // Template management operations
    WorkplanTemplate createNewTemplate(WorkplanTemplate template, String createdBy);
    
    WorkplanTemplate updateTemplate(WorkplanTemplate template);
    
    WorkplanTemplate activateTemplate(String evaluationPeriod);
    
    // Utility operations
    List<WorkplanTemplate> getArchivedTemplatesByStatus(String status);
    
    boolean hasActiveTemplate();
    
    WorkplanTemplate getTemplateById(Long id);
    
    void deleteTemplate(Long id);
}
