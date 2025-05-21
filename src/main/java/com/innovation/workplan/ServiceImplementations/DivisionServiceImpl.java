package com.innovation.workplan.ServiceImplementations;

import com.innovation.workplan.CollectionModels.Division;
import com.innovation.workplan.CollectionModels.Pillars;
import com.innovation.workplan.Repositories.DivisionRepository;
import com.innovation.workplan.Services.DivisionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DivisionServiceImpl implements DivisionService {
    @Autowired
    DivisionRepository divisionRepository;

    @Autowired
    MongoTemplate mongoTemplate;
    @Autowired
    SequenceGeneratorService sequenceGenerator;
    @Override
    public ResponseEntity<String> saveDivision(Division division){
        Query query = new Query();
        query.addCriteria(Criteria.where("divisionName").is(division.getDivisionName()));
        if (mongoTemplate.exists(query, "divisions")){
                    return ResponseEntity.status(400).body("Division already exists!");
                }
        else {
                 division.setId(sequenceGenerator.generateSequence(Division.SEQUENCE_NAME));
                 divisionRepository.save(division);
                 return ResponseEntity.status(200).body("Division Saved");
                }
            }

    public ResponseEntity<List<Division>> getAll() {

        return ResponseEntity.status(200).body(divisionRepository.findAll());
    }

    public ResponseEntity<List<Division>> getActiveDivisions(){
        Query query = new Query();
        query.addCriteria(Criteria.where("active").is(true));
        return ResponseEntity.status(200).body(mongoTemplate.find(query, Division.class, "divisions"));
    }

    public ResponseEntity<String> deactivateDivision(String division){
        Query query = new Query();
        query.addCriteria(Criteria.where("divisionName").is(division));

        Update update = new Update();
        update.set("active", false);
        mongoTemplate.findAndModify(query, update, new FindAndModifyOptions().returnNew(true), Division.class);
        return ResponseEntity.status(200).body("Division Deactivated");
    }

    public ResponseEntity<String> updateDivision(Division division){
        Query query = new Query();
        query.addCriteria(Criteria.where("divisionName").is(division.getDivisionName()));

        Query query1 = new Query();
            query1.addCriteria(Criteria.where("id").is(division.getId()));
        Update updateDef = new Update();
        updateDef.set("divisionName", division.getDivisionName());
        mongoTemplate.findAndModify(query1, updateDef, new FindAndModifyOptions().returnNew(true), Division.class);
        return ResponseEntity.status(200).body("Division Updated");
    }

    public ResponseEntity<String> updateSection(String divisionName, String oldSectionName, String newSectionName){
        Query query1 = new Query();
        query1.addCriteria(Criteria.where("divisionName").is(divisionName));

        Update update = new Update();
        Update updateDef = new Update();
        update.pull("sectionName", oldSectionName);
        updateDef.addToSet("sectionName", newSectionName);
        mongoTemplate.findAndModify(query1, updateDef, new FindAndModifyOptions().returnNew(true), Division.class);
        mongoTemplate.findAndModify(query1, update, new FindAndModifyOptions().returnNew(true), Division.class);

        return ResponseEntity.status(200).body("Section Updated");
    }

    public ResponseEntity<String> deleteDivision(Long Id){
        divisionRepository.deleteById(Id);
        return ResponseEntity.status(200).body("Division Deleted");
    }

    public ResponseEntity<String> deleteSection(String division, String section){
        Query query = new Query();

            query.addCriteria(Criteria.where("divisionName").is(division));
            Update update = new Update();
        Division a = mongoTemplate.findOne(query, Division.class, "divisions");
        System.out.println(a.getSectionName().remove(section));
           update.pull("sectionName", section);
        mongoTemplate.findAndModify(query, update, new FindAndModifyOptions().returnNew(true), Division.class);

        return ResponseEntity.status(200).body("Section Deleted");
    }

    public ResponseEntity<String> addSection(String sectionName, String divisionName) {
        Query query = new Query();
        query.addCriteria(Criteria.where("divisionName").is(divisionName));

        Query query1 = new Query();
        query1.addCriteria(Criteria.where("sectionName").is(sectionName));
        if (mongoTemplate.exists(query1, "divisions")) {
            return ResponseEntity.status(400).body("Section already exists");
        } else {
            Update updateDef = new Update();
            updateDef.addToSet("sectionName", sectionName);
            mongoTemplate.findAndModify(query, updateDef, new FindAndModifyOptions().returnNew(true), Division.class);
            return ResponseEntity.status(200).body("Section is saved");
        }
    }


}
