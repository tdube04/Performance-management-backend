package com.innovation.workplan.ServiceImplementations;

import com.innovation.workplan.CollectionModels.*;
import com.innovation.workplan.Repositories.PercentageRepository;
import com.innovation.workplan.Repositories.PerformanceAreaRepository;
import com.innovation.workplan.Services.PercentageService;
import com.innovation.workplan.Services.PerformanceAreaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class PerformanceAreaServiceImpl implements PerformanceAreaService {

    @Autowired
    PerformanceAreaRepository performanceAreaRepository;

    @Autowired
    PercentageRepository percentageRepository;

    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    SequenceGeneratorService sequenceGenerator;

    @Autowired
    PercentageService percentageService;

    public ResponseEntity<String> savePerformanceArea(@RequestBody PerformanceArea performanceArea){
        Query query = new Query();
        query.addCriteria(Criteria.where("performanceArea").is(performanceArea.getPerformanceArea()));

        Query query1 = new Query();
        query1.addCriteria(Criteria.where("section").is(performanceArea.getSection()));

        // Check if collection exists, if not, just proceed to save
        try {
            if (mongoTemplate.exists(query1, "performanceArea")){
                return ResponseEntity.status(400).body("Section already exists");
            }
        } catch (Exception e) {
            // Collection doesn't exist, will be created
        }

        try {
            if (performanceArea.getWeight() <= 100 && !mongoTemplate.exists(query, "performanceArea")){
                return percentageService.savePercentage(performanceArea);
            }
            else
                return ResponseEntity.status(400).body("Performance area already exists");
        } catch (Exception e) {
            // Collection doesn't exist, this is the first entry - proceed to save
            return percentageService.savePercentage(performanceArea);
        }
    }

    public ResponseEntity<List<PerformanceArea>> getAll() {
        try {
            return ResponseEntity.status(200).body(performanceAreaRepository.findAll());
        } catch (Exception e) {
            // Collection doesn't exist yet, return empty list
            return ResponseEntity.status(200).body(new ArrayList<>());
        }
    }

    public ResponseEntity<List<PerformanceArea>> getActivePerformanceAreas(){
        Query query = new Query();
        query.addCriteria(Criteria.where("active").is(true));
        try {
            return ResponseEntity.status(200).body(mongoTemplate.find(query, PerformanceArea.class, "performanceArea"));
        } catch (Exception e) {
            // Collection doesn't exist yet, return empty list
            return ResponseEntity.status(200).body(new ArrayList<>());
        }
    }

    public ResponseEntity<PerformanceArea> getByPerformanceArea(String performanceArea){
        Query query = new Query();
        query.addCriteria(Criteria.where("performanceArea").is(performanceArea));

        try {
            return ResponseEntity.status(200).body(mongoTemplate.findOne(query, PerformanceArea.class, "performanceArea"));
        } catch (Exception e) {
            // Collection doesn't exist
            return ResponseEntity.status(200).body(null);
        }
    }

    public ResponseEntity<PerformanceArea> getByYear(int year){
        Query query = new Query();
        query.addCriteria(Criteria.where("year").is(year));

        try {
            return ResponseEntity.status(200).body(mongoTemplate.findOne(query, PerformanceArea.class, "performanceArea"));
        } catch (Exception e) {
            // Collection doesn't exist
            return ResponseEntity.status(200).body(null);
        }
    }

    public ResponseEntity<String> deactivatePerformanceArea(String performanceArea){
        Query query = new Query();
        query.addCriteria(Criteria.where("performanceArea").is(performanceArea));

        Percentage percentage = new Percentage();

        try {
            PerformanceArea area = mongoTemplate.findOne(query, PerformanceArea.class, "performanceArea");
            
            if (area == null) {
                return ResponseEntity.status(404).body("Performance area not found");
            }

            Query query2 = new Query();
            query2.addCriteria(Criteria.where("_id").is(Percentage.SEQUENCE_NAME));
            DatabaseSequence idList = mongoTemplate.findOne(query2, DatabaseSequence.class, "database_sequences");

            if (idList == null) {
                return ResponseEntity.status(405).body("Sequence not found, please initialize the database first");
            }

            Query query1 = new Query();
            query1.addCriteria(Criteria.where("_id").is(idList.getSeq()));
            Percentage percentageList = mongoTemplate.findOne(query1, Percentage.class, "percentage");

            if (percentageList == null) {
                return ResponseEntity.status(405).body("Percentage record not found");
            }

            percentage.setPercent(percentageList.getPercent() - area.getWeight());
            percentage.setId(sequenceGenerator.generateSequence(Percentage.SEQUENCE_NAME));
            percentageRepository.save(percentage);
        }
        catch (NullPointerException e) {
            return ResponseEntity.status(405).body("One Id was not found, check database");
        }

        Update update = new Update();
        update.set("active", false);
        mongoTemplate.findAndModify(query, update, new FindAndModifyOptions().returnNew(true), PerformanceArea.class);
        return ResponseEntity.status(200).body("Performance Area Deactivated");
    }

    public ResponseEntity<String> updatePerformanceArea(@RequestBody PerformanceArea performanceArea) {

        Percentage percentage = new Percentage();

        Query query4 = new Query();
        query4.addCriteria(Criteria.where("performanceArea").is(performanceArea.getPerformanceArea()));

        Query query3 = new Query();
        query3.addCriteria(Criteria.where("section").is(performanceArea.getSection()));

//        Query query5 = new Query();
//        query5.addCriteria(Criteria.where("programs.programName").is(performanceArea.getPrograms().get(0).getProgramName()));
//        if (mongoTemplate.exists(query5, PerformanceArea.class, "performanceArea")) {
//            return ResponseEntity.status(400).body("Program already exists");
//        }
//
//        if (mongoTemplate.exists(query3, "performanceArea")) {
//            return ResponseEntity.status(400).body("Section already exists");
//        }
//
//        if (mongoTemplate.exists(query4, "performanceArea")) {
//            return ResponseEntity.status(400).body("Performance area already exists");
//        }

        Query query6 = new Query();
        query6.addCriteria(Criteria.where("performanceArea").is(performanceArea));
        PerformanceArea b = mongoTemplate.findOne(query6, PerformanceArea.class, "performanceArea");
        List<PerformanceArea> a = mongoTemplate.find(query6, PerformanceArea.class, "performanceArea");

        double totalWeight = 0;
        if(b != null) {
            if (performanceArea.getPrograms().get(0).getWeight() <= b.getWeight()) {
                if (!a.isEmpty()) {
                    for (PerformanceArea area : a) {
                        if (area.getPrograms() != null) {
                            for (AdminProgram prog : area.getPrograms()) {
                                totalWeight = totalWeight + prog.getWeight();
                                if (totalWeight + performanceArea.getPrograms().get(0).getWeight() > b.getWeight()) {
                                    return ResponseEntity.status(400).body("program weight is above performance area weight. please adjust weight and try again");
                                }
                            }
                        }
                    }
                }
            }
        }


        Query query1 = new Query();
        query1.addCriteria(Criteria.where("_id").is(Percentage.SEQUENCE_NAME));
        DatabaseSequence idList = mongoTemplate.findOne(query1, DatabaseSequence.class, "database_sequences");

        int difference;
        try {
            Query query2 = new Query();
            query2.addCriteria(Criteria.where("_id").is(idList.getSeq()));
            Percentage percentageList = mongoTemplate.findOne(query2, Percentage.class, "percentage");


            Query query = new Query();
            query.addCriteria(Criteria.where("_id").is(performanceArea.getId()));

            PerformanceArea performanceList = mongoTemplate.findOne(query, PerformanceArea.class, "performanceArea");

            difference = performanceList.getWeight() - performanceArea.getWeight();
            Update updateDef = new Update();

            if (percentageList.getPercent() + difference <= 100) {
                updateDef.set("PerformanceArea", performanceArea.getPerformanceArea());
                updateDef.set("section", performanceArea.getSection());
                updateDef.set("programs", performanceArea.getPrograms());
                //updateDef.set("Description", performanceArea.getDescription());
                updateDef.set("weight", performanceArea.getWeight());
                mongoTemplate.findAndModify(query, updateDef, new FindAndModifyOptions().returnNew(true), PerformanceArea.class);

                percentage.setPercent(difference + percentageList.getPercent());
                percentage.setId(sequenceGenerator.generateSequence(Percentage.SEQUENCE_NAME));
                percentageRepository.save(percentage);
                return ResponseEntity.status(200).body("Performance Area Updated");
            }
        } catch (NullPointerException nullPointerException) {
            return ResponseEntity.status(405).body("sequence has not been added yet, performance area has not been null");
        }
        return ResponseEntity.status(200).body("Performance area weight is greater than 100% by " + difference + "%");
    }

    public ResponseEntity<String> deletePerformanceArea(Long Id){
        Percentage percentage = new Percentage();

        try {
            Query query = new Query();
            query.addCriteria(Criteria.where("_id").is(Id));
            PerformanceArea performanceArea = mongoTemplate.findOne(query, PerformanceArea.class, "performanceArea");
            
            if (performanceArea == null) {
                return ResponseEntity.status(404).body("Performance area not found");
            }

            Query query2 = new Query();
            query2.addCriteria(Criteria.where("_id").is(Percentage.SEQUENCE_NAME));
            DatabaseSequence idList = mongoTemplate.findOne(query2, DatabaseSequence.class, "database_sequences");
            
            if (idList == null) {
                return ResponseEntity.status(405).body("Sequence not found, please initialize the database first");
            }

            Query query1 = new Query();
            query1.addCriteria(Criteria.where("_id").is(idList.getSeq()));
            Percentage percentageList = mongoTemplate.findOne(query1, Percentage.class, "percentage");
            
            if (percentageList == null) {
                return ResponseEntity.status(405).body("Percentage record not found");
            }

            percentage.setPercent(percentageList.getPercent() - performanceArea.getWeight());
            percentage.setId(sequenceGenerator.generateSequence(Percentage.SEQUENCE_NAME));
            percentageRepository.save(percentage);
        }
        catch (NullPointerException e) {
            return ResponseEntity.status(405).body("One Id was not found, check database");
        }
        performanceAreaRepository.deleteById(Id);
        return ResponseEntity.status(200).body("Performance Area deleted");
    }

    public ResponseEntity<String> deleteProgram(AdminProgram program){
        Query query = new Query();

        query.addCriteria(Criteria.where("programs.programName").is(program.getProgramName()));
        Update update = new Update();
        update.pull("programs", program);
        mongoTemplate.findAndModify(query, update, new FindAndModifyOptions().returnNew(true), PerformanceArea.class);
        return ResponseEntity.status(200).body("Program Deleted");

    }

    public ResponseEntity<String> addProgram(@RequestBody AdminProgram program, String performanceArea){
        Query query = new Query();
        query.addCriteria(Criteria.where("performanceArea").is(performanceArea));
        PerformanceArea b = mongoTemplate.findOne(query, PerformanceArea.class, "performanceArea");
        List<PerformanceArea> a = mongoTemplate.find(query, PerformanceArea.class, "performanceArea");

        Query query1 = new Query();
        query1.addCriteria(Criteria.where("programs.programName").is(program.getProgramName()));
        if (mongoTemplate.exists(query1, PerformanceArea.class, "performanceArea")){
            return ResponseEntity.status(400).body("Program already exists");
        }

        double totalWeight = 0;
        if(b != null) {
            if (program.getWeight() <= b.getWeight()) {
                if (!a.isEmpty()){
                    for (PerformanceArea area : a) {
                        if (area.getPrograms() != null) {
                            for (AdminProgram prog : area.getPrograms()) {
                            totalWeight = totalWeight + prog.getWeight();
                            if (totalWeight + program.getWeight() > b.getWeight()) {
                                return ResponseEntity.status(400).body("Please adjust weight and try again");
                            }
                        }
                        }
                        else {
                            Update updateDef = new Update();
                            updateDef.set("programs", Arrays.asList(program));
                            mongoTemplate.findAndModify(query, updateDef, new FindAndModifyOptions().returnNew(true), PerformanceArea.class);
                            return ResponseEntity.status(200).body("Program Added Successfully");
                        }
                    }
                        Update updateDef = new Update();
                        updateDef.addToSet("programs", program);
                        mongoTemplate.findAndModify(query, updateDef, new FindAndModifyOptions().returnNew(true), PerformanceArea.class);
                }
                else {
                    System.out.println("hie");
                    Update updateDef = new Update();
                    updateDef.set("programs", Arrays.asList(program));
                    mongoTemplate.findAndModify(query, updateDef, new FindAndModifyOptions().returnNew(true), PerformanceArea.class);
                }
                return ResponseEntity.status(200).body("Program Added Successfully");
            }
            else {
                return ResponseEntity.status(400).body("program weight is above performance area weight. please adjust weight and try again");
            }
        }
        else {
            return ResponseEntity.status(400).body("performance area does not exist");
        }

    }

}
