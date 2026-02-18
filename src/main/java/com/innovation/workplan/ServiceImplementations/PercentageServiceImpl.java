package com.innovation.workplan.ServiceImplementations;

import com.innovation.workplan.CollectionModels.DatabaseSequence;
import com.innovation.workplan.CollectionModels.Percentage;
import com.innovation.workplan.CollectionModels.PerformanceArea;
import com.innovation.workplan.Repositories.PercentageRepository;
import com.innovation.workplan.Repositories.PerformanceAreaRepository;
import com.innovation.workplan.Services.PercentageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


@Service
public class PercentageServiceImpl implements PercentageService {

    @Autowired
    PercentageRepository percentageRepository;
    @Autowired
    SequenceGeneratorService sequenceGenerator;
    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    PerformanceAreaRepository performanceAreaRepository;


    @Override
    public ResponseEntity<String> savePercentage(PerformanceArea performanceArea) {
        Percentage percentage = new Percentage();
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(Percentage.SEQUENCE_NAME));
        
        // Check if database_sequences collection exists and has the sequence
        DatabaseSequence idList = null;
        try {
            idList = mongoTemplate.findOne(query, DatabaseSequence.class, "database_sequences");
        } catch (Exception e) {
            // Collection doesn't exist, will create it
            idList = null;
        }

        if (idList == null) {
            // First time - initialize the sequence and create the performance area
            percentage.setPercent(performanceArea.getWeight());
            percentage.setId(sequenceGenerator.generateSequence(Percentage.SEQUENCE_NAME));
            percentageRepository.save(percentage);
            performanceArea.setId(sequenceGenerator.generateSequence(PerformanceArea.SEQUENCE_NAME));
            performanceAreaRepository.save(performanceArea);
            return ResponseEntity.status(200).body("Performance Area Saved");
        } else {
            Query query1 = new Query();
            query1.addCriteria(Criteria.where("_id").is(idList.getSeq()));
            
            // Check if percentage collection exists and has data
            Percentage percentageList = null;
            try {
                percentageList = mongoTemplate.findOne(query1, Percentage.class, "percentage");
            } catch (Exception e) {
                // Collection doesn't exist, treat as first entry
                percentage.setPercent(performanceArea.getWeight());
                percentage.setId(sequenceGenerator.generateSequence(Percentage.SEQUENCE_NAME));
                percentageRepository.save(percentage);
                performanceArea.setId(sequenceGenerator.generateSequence(PerformanceArea.SEQUENCE_NAME));
                performanceAreaRepository.save(performanceArea);
                return ResponseEntity.status(200).body("Performance Area Saved");
            }

            if (percentageList == null) {
                // No percentage record found, treat as first entry
                percentage.setPercent(performanceArea.getWeight());
                percentage.setId(sequenceGenerator.generateSequence(Percentage.SEQUENCE_NAME));
                percentageRepository.save(percentage);
                performanceArea.setId(sequenceGenerator.generateSequence(PerformanceArea.SEQUENCE_NAME));
                performanceAreaRepository.save(performanceArea);
                return ResponseEntity.status(200).body("Performance Area Saved");
            }

            try {
                if (percentageList.getPercent() >= 100 || percentageList.getPercent() + performanceArea.getWeight() > 100) {
                    return ResponseEntity.status(400).body("total weight is " + (percentageList.getPercent() + performanceArea.getWeight()) + " ,weights are above 100");
                }
                else {
                    percentage.setPercent(performanceArea.getWeight() + percentageList.getPercent());
                    percentage.setId(sequenceGenerator.generateSequence(Percentage.SEQUENCE_NAME));
                    performanceArea.setId(sequenceGenerator.generateSequence(PerformanceArea.SEQUENCE_NAME));
                    performanceAreaRepository.save(performanceArea);
                    percentageRepository.save(percentage);
                    return ResponseEntity.status(200).body("Performance Area Saved");
                }
            }
            catch (NullPointerException e){
                return ResponseEntity.status(405).body("One Id is missing, check database");
            }
        }
    }
}
