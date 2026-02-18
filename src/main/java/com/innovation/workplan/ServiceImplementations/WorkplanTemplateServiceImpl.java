package com.innovation.workplan.ServiceImplementations;

import com.innovation.workplan.CollectionModels.*;
import com.innovation.workplan.Repositories.WorkplanTemplateRepository;
import com.innovation.workplan.Services.WorkplanTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class WorkplanTemplateServiceImpl implements WorkplanTemplateService {

    @Autowired
    private WorkplanTemplateRepository workplanTemplateRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private SequenceGeneratorService sequenceGenerator;

    /**
     * Archive the current active performance areas as a template
     * This is called when closing a quarter
     */
    @Override
    public WorkplanTemplate archiveCurrentTemplate(String evaluationPeriod, String archivedBy, String archiveReason) {
        // Get all performance areas (not just active ones) - archive all for historical reference
        List<PerformanceArea> allAreas = mongoTemplate.findAll(PerformanceArea.class, "performanceArea");

        // Convert PerformanceArea to TemplatePerformanceArea
        ArrayList<WorkplanTemplate.TemplatePerformanceArea> templateAreas = new ArrayList<>();
        double totalWeights = 0;

        for (PerformanceArea area : allAreas) {
            WorkplanTemplate.TemplatePerformanceArea templateArea = WorkplanTemplate.TemplatePerformanceArea.builder()
                .performanceArea(area.getPerformanceArea())
                .section(area.getSection())
                .description(area.getDescription())
                .weight(area.getWeight())
                .pillars(area.getPillars())
                .programs(convertPrograms(area.getPrograms()))
                .build();
            
            templateAreas.add(templateArea);
            totalWeights += area.getWeight();
        }

        // Parse year and quarter from evaluationPeriod (e.g., "2026-Q1")
        String[] parts = evaluationPeriod.split("-");
        int year = Integer.parseInt(parts[0]);
        String quarter = parts[1];

        // Create the archived template
        WorkplanTemplate template = WorkplanTemplate.builder()
            .id(sequenceGenerator.generateSequence(WorkplanTemplate.SEQUENCE_NAME))
            .templateName("Workplan Template " + evaluationPeriod)
            .evaluationPeriod(evaluationPeriod)
            .year(year)
            .quarter(quarter)
            .templateStatus("ARCHIVED")
            .performanceAreas(templateAreas)
            .createdAt(LocalDateTime.now())
            .archivedAt(LocalDateTime.now())
            .archivedBy(archivedBy)
            .archiveReason(archiveReason)
            .totalPerformanceAreas(templateAreas.size())
            .totalWeights(totalWeights)
            .version("1.0")
            .build();

        return workplanTemplateRepository.save(template);
    }

    /**
     * Convert AdminProgram to TemplateProgram
     */
    private ArrayList<WorkplanTemplate.TemplateProgram> convertPrograms(ArrayList<AdminProgram> programs) {
        ArrayList<WorkplanTemplate.TemplateProgram> templatePrograms = new ArrayList<>();
        if (programs != null) {
            for (AdminProgram program : programs) {
                WorkplanTemplate.TemplateProgram templateProgram = WorkplanTemplate.TemplateProgram.builder()
                    .programName(program.getProgramName())
                    .weight(program.getWeight())
                    .contributedPillar(program.getContributedPillar())
                    .build();
                templatePrograms.add(templateProgram);
            }
        }
        return templatePrograms;
    }

    @Override
    public List<WorkplanTemplate> getAllArchivedTemplates() {
        return workplanTemplateRepository.findAllByOrderByEvaluationPeriodDesc();
    }

    @Override
    public List<WorkplanTemplate> getTemplatesByYear(int year) {
        return workplanTemplateRepository.findByYearOrderByQuarterDesc(year);
    }

    @Override
    public WorkplanTemplate getTemplateByEvaluationPeriod(String evaluationPeriod) {
        Optional<WorkplanTemplate> template = workplanTemplateRepository.findByEvaluationPeriod(evaluationPeriod);
        return template.orElse(null);
    }

    @Override
    public WorkplanTemplate getActiveTemplate() {
        Optional<WorkplanTemplate> template = workplanTemplateRepository.findTopByTemplateStatusOrderByCreatedAtDesc("ACTIVE");
        return template.orElse(null);
    }

    @Override
    public WorkplanTemplate createNewTemplate(WorkplanTemplate template, String createdBy) {
        // Deactivate any existing active templates
        List<WorkplanTemplate> activeTemplates = workplanTemplateRepository.findByTemplateStatusOrderByArchivedAtDesc("ACTIVE");
        for (WorkplanTemplate activeTemplate : activeTemplates) {
            activeTemplate.setTemplateStatus("ARCHIVED");
            activeTemplate.setArchivedAt(LocalDateTime.now());
            workplanTemplateRepository.save(activeTemplate);
        }

        // Set template metadata
        template.setId(sequenceGenerator.generateSequence(WorkplanTemplate.SEQUENCE_NAME));
        template.setCreatedAt(LocalDateTime.now());
        template.setCreatedBy(createdBy);
        template.setTemplateStatus("ACTIVE");

        // Calculate totals
        if (template.getPerformanceAreas() != null) {
            template.setTotalPerformanceAreas(template.getPerformanceAreas().size());
            double totalWeights = template.getPerformanceAreas().stream()
                .mapToDouble(WorkplanTemplate.TemplatePerformanceArea::getWeight)
                .sum();
            template.setTotalWeights(totalWeights);
        }

        return workplanTemplateRepository.save(template);
    }

    @Override
    public WorkplanTemplate updateTemplate(WorkplanTemplate template) {
        Optional<WorkplanTemplate> existing = workplanTemplateRepository.findById(template.getId());
        if (existing.isPresent()) {
            WorkplanTemplate updated = existing.get();
            updated.setTemplateName(template.getTemplateName());
            updated.setPerformanceAreas(template.getPerformanceAreas());
            
            // Recalculate totals
            if (template.getPerformanceAreas() != null) {
                updated.setTotalPerformanceAreas(template.getPerformanceAreas().size());
                double totalWeights = template.getPerformanceAreas().stream()
                    .mapToDouble(WorkplanTemplate.TemplatePerformanceArea::getWeight)
                    .sum();
                updated.setTotalWeights(totalWeights);
            }
            
            return workplanTemplateRepository.save(updated);
        }
        return null;
    }

    @Override
    public WorkplanTemplate activateTemplate(String evaluationPeriod) {
        Optional<WorkplanTemplate> templateOpt = workplanTemplateRepository.findByEvaluationPeriod(evaluationPeriod);
        if (templateOpt.isPresent()) {
            WorkplanTemplate template = templateOpt.get();
            
            // Deactivate any existing active templates
            List<WorkplanTemplate> activeTemplates = workplanTemplateRepository.findByTemplateStatusOrderByArchivedAtDesc("ACTIVE");
            for (WorkplanTemplate activeTemplate : activeTemplates) {
                activeTemplate.setTemplateStatus("ARCHIVED");
                activeTemplate.setArchivedAt(LocalDateTime.now());
                workplanTemplateRepository.save(activeTemplate);
            }
            
            // Activate the selected template
            template.setTemplateStatus("ACTIVE");
            return workplanTemplateRepository.save(template);
        }
        return null;
    }

    @Override
    public List<WorkplanTemplate> getArchivedTemplatesByStatus(String status) {
        return workplanTemplateRepository.findByTemplateStatusOrderByArchivedAtDesc(status);
    }

    @Override
    public boolean hasActiveTemplate() {
        return workplanTemplateRepository.findTopByTemplateStatusOrderByCreatedAtDesc("ACTIVE").isPresent();
    }

    @Override
    public WorkplanTemplate getTemplateById(Long id) {
        Optional<WorkplanTemplate> template = workplanTemplateRepository.findById(id);
        return template.orElse(null);
    }

    @Override
    public void deleteTemplate(Long id) {
        workplanTemplateRepository.deleteById(id);
    }
}
