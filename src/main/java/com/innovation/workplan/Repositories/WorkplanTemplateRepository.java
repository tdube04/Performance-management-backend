package com.innovation.workplan.Repositories;

import com.innovation.workplan.CollectionModels.WorkplanTemplate;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkplanTemplateRepository extends MongoRepository<WorkplanTemplate, Long> {
    
    Optional<WorkplanTemplate> findByEvaluationPeriod(String evaluationPeriod);
    
    Optional<WorkplanTemplate> findByTemplateStatus(String templateStatus);
    
    List<WorkplanTemplate> findByTemplateStatusOrderByArchivedAtDesc(String templateStatus);
    
    List<WorkplanTemplate> findByYearOrderByQuarterDesc(int year);
    
    Optional<WorkplanTemplate> findTopByTemplateStatusOrderByCreatedAtDesc(String templateStatus);
    
    boolean existsByEvaluationPeriod(String evaluationPeriod);
    
    List<WorkplanTemplate> findAllByOrderByEvaluationPeriodDesc();
}
