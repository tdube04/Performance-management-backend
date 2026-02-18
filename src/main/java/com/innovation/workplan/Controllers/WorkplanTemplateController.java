package com.innovation.workplan.Controllers;

import com.innovation.workplan.CollectionModels.WorkplanTemplate;
import com.innovation.workplan.Services.WorkplanTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Workplan Template archival and management operations
 */
@RestController
@RequestMapping("/workplan-template")
@CrossOrigin(origins = "*")
public class WorkplanTemplateController {

    @Autowired
    private WorkplanTemplateService workplanTemplateService;

    /**
     * Get all archived templates
     * GET /workplan-template/all
     */
    @GetMapping("/all")
    public ResponseEntity<List<WorkplanTemplate>> getAllArchivedTemplates() {
        List<WorkplanTemplate> templates = workplanTemplateService.getAllArchivedTemplates();
        return ResponseEntity.ok(templates);
    }

    /**
     * Get templates by year
     * GET /workplan-template/year/{year}
     */
    @GetMapping("/year/{year}")
    public ResponseEntity<List<WorkplanTemplate>> getTemplatesByYear(@PathVariable int year) {
        List<WorkplanTemplate> templates = workplanTemplateService.getTemplatesByYear(year);
        return ResponseEntity.ok(templates);
    }

    /**
     * Get active template
     * GET /workplan-template/active
     */
    @GetMapping("/active")
    public ResponseEntity<WorkplanTemplate> getActiveTemplate() {
        WorkplanTemplate template = workplanTemplateService.getActiveTemplate();
        if (template != null) {
            return ResponseEntity.ok(template);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Get template by evaluation period
     * GET /workplan-template/period/{evaluationPeriod}
     */
    @GetMapping("/period/{evaluationPeriod}")
    public ResponseEntity<WorkplanTemplate> getTemplateByEvaluationPeriod(@PathVariable String evaluationPeriod) {
        WorkplanTemplate template = workplanTemplateService.getTemplateByEvaluationPeriod(evaluationPeriod);
        if (template != null) {
            return ResponseEntity.ok(template);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Get template by ID
     * GET /workplan-template/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<WorkplanTemplate> getTemplateById(@PathVariable Long id) {
        WorkplanTemplate template = workplanTemplateService.getTemplateById(id);
        if (template != null) {
            return ResponseEntity.ok(template);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Delete a template
     * DELETE /workplan-template/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTemplate(@PathVariable Long id) {
        workplanTemplateService.deleteTemplate(id);
        return ResponseEntity.ok().build();
    }

    /**
     * Archive current template (manual trigger)
     * POST /workplan-template/archive
     * Body: { "evaluationPeriod": "2026-Q1", "archivedBy": "admin", "archiveReason": "Manual archive" }
     */
    @PostMapping("/archive")
    public ResponseEntity<WorkplanTemplate> archiveCurrentTemplate(@RequestBody ArchiveRequest request) {
        WorkplanTemplate template = workplanTemplateService.archiveCurrentTemplate(
            request.getEvaluationPeriod(),
            request.getArchivedBy(),
            request.getArchiveReason()
        );
        return ResponseEntity.ok(template);
    }

    /**
     * Create new template
     * POST /workplan-template/create
     */
    @PostMapping("/create")
    public ResponseEntity<WorkplanTemplate> createNewTemplate(@RequestBody WorkplanTemplate template, 
                                                               @RequestParam String createdBy) {
        WorkplanTemplate created = workplanTemplateService.createNewTemplate(template, createdBy);
        return ResponseEntity.ok(created);
    }

    /**
     * Update template
     * PUT /workplan-template/update
     */
    @PutMapping("/update")
    public ResponseEntity<WorkplanTemplate> updateTemplate(@RequestBody WorkplanTemplate template) {
        WorkplanTemplate updated = workplanTemplateService.updateTemplate(template);
        if (updated != null) {
            return ResponseEntity.ok(updated);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Activate a specific template
     * PUT /workplan-template/activate/{evaluationPeriod}
     */
    @PutMapping("/activate/{evaluationPeriod}")
    public ResponseEntity<WorkplanTemplate> activateTemplate(@PathVariable String evaluationPeriod) {
        WorkplanTemplate template = workplanTemplateService.activateTemplate(evaluationPeriod);
        if (template != null) {
            return ResponseEntity.ok(template);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Check if active template exists
     * GET /workplan-template/has-active
     */
    @GetMapping("/has-active")
    public ResponseEntity<Boolean> hasActiveTemplate() {
        return ResponseEntity.ok(workplanTemplateService.hasActiveTemplate());
    }

    /**
     * Get archived templates by status
     * GET /workplan-template/status/{status}
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<WorkplanTemplate>> getArchivedTemplatesByStatus(@PathVariable String status) {
        List<WorkplanTemplate> templates = workplanTemplateService.getArchivedTemplatesByStatus(status);
        return ResponseEntity.ok(templates);
    }

    /**
     * Request body class for archive operation
     */
    public static class ArchiveRequest {
        private String evaluationPeriod;
        private String archivedBy;
        private String archiveReason;

        public String getEvaluationPeriod() {
            return evaluationPeriod;
        }

        public void setEvaluationPeriod(String evaluationPeriod) {
            this.evaluationPeriod = evaluationPeriod;
        }

        public String getArchivedBy() {
            return archivedBy;
        }

        public void setArchivedBy(String archivedBy) {
            this.archivedBy = archivedBy;
        }

        public String getArchiveReason() {
            return archiveReason;
        }

        public void setArchiveReason(String archiveReason) {
            this.archiveReason = archiveReason;
        }
    }
}
