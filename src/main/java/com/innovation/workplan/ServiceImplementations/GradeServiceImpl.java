package com.innovation.workplan.ServiceImplementations;

import com.innovation.workplan.CollectionModels.Grade;
import com.innovation.workplan.Repositories.GradeRepository;
import com.innovation.workplan.Services.GradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Service
public class GradeServiceImpl implements GradeService {
    @Autowired
    GradeRepository gradeRepository;

    @Autowired
    SequenceGeneratorService sequenceGenerator;
    @Autowired
    MongoTemplate mongoTemplate;

    public ResponseEntity<String> saveGrade(@RequestBody Grade grade){
        Query query = new Query();
        query.addCriteria(Criteria.where("Grade").is(grade.getGrade()));
        if (mongoTemplate.exists(query, "grade")){
            return ResponseEntity.status(400).body("Grade with same number already exists!");
        }
        else {
                    grade.setId(sequenceGenerator.generateSequence(Grade.SEQUENCE_NAME));
                    gradeRepository.save(grade);
                    return ResponseEntity.status(200).body("Grade Saved!");
                }
            }

    public ResponseEntity<List<Grade>> getAll() {
        return ResponseEntity.status(200).body(gradeRepository.findAll());
    }

    public ResponseEntity<String> deleteGrade(Long Id){
        gradeRepository.deleteById(Id);
        return ResponseEntity.status(200).body("grade deleted");
    }
}
