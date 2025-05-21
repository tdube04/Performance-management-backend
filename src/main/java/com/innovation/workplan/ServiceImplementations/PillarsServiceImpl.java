package com.innovation.workplan.ServiceImplementations;

import com.innovation.workplan.CollectionModels.Division;
import com.innovation.workplan.CollectionModels.Pillars;
import com.innovation.workplan.Repositories.PillarsRepository;
import com.innovation.workplan.Services.PillarsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class PillarsServiceImpl implements PillarsService {

    @Autowired
    SequenceGeneratorService sequenceGenerator;

    @Autowired
    PillarsRepository pillarsRepository;

    @Autowired
    MongoTemplate mongoTemplate;


    public ResponseEntity<String> savePillar(@RequestBody Pillars pillars) {
        Query query = new Query();
        query.addCriteria(Criteria.where("Pillar").is(pillars.getPillar()));

        if (mongoTemplate.exists(query, "pillars")) {
            return ResponseEntity.status(400).body("Pillar already exists!");
        } else {
            pillars.setId(sequenceGenerator.generateSequence(Division.SEQUENCE_NAME));
            pillarsRepository.save(pillars);
            return ResponseEntity.status(200).body("Pillar Saved");
        }
    }

    public ResponseEntity<List<Pillars>> getActivePillars(){
        Query query = new Query();
        query.addCriteria(Criteria.where("active").is(true));
        return ResponseEntity.status(200).body(mongoTemplate.find(query, Pillars.class, "pillars"));
    }

    public ResponseEntity<String> deactivatePillars(String pillar){
        Query query = new Query();
        query.addCriteria(Criteria.where("Pillar").is(pillar));

        Update update = new Update();
        update.set("active", false);
        mongoTemplate.findAndModify(query, update, new FindAndModifyOptions().returnNew(true), Pillars.class);
        return ResponseEntity.status(200).body("Pillar Deactivated");
    }

    public ResponseEntity<List<Pillars>> getAll() {

        return ResponseEntity.status(200).body(pillarsRepository.findAll());
    }

    @Override
    public ResponseEntity<String> getAllByYear(int year) {

        List<Pillars> date = new ArrayList<>();
        for (Pillars x : pillarsRepository.findAll()){
           if (x.getCurrent_date().getYear() == year){
               date.add(x);
           }
        }
        if (date.isEmpty())
            return ResponseEntity.status(400).body("No Pillars for current year");

        return ResponseEntity.status(200).body(String.valueOf(date));
    }

    public ResponseEntity<String> updatePillars(@RequestBody Pillars pillars){
        Query query1 = new Query();
        query1.addCriteria(Criteria.where("Pillar").is(pillars.getPillar()));

        if (mongoTemplate.exists(query1, "pillars")) {
            return ResponseEntity.status(400).body("Pillar already exists!");
        }
        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(pillars.getId()));

        Update updateDef = new Update();
        updateDef.set("Pillar", pillars.getPillar());
        updateDef.set("Description", pillars.getDescription());
        mongoTemplate.findAndModify(query, updateDef, new FindAndModifyOptions().returnNew(true), Pillars.class);
        return ResponseEntity.status(200).body("Successfully Updated");
    }

    public ResponseEntity<String> deletePillar(Long Id){
        pillarsRepository.deleteById(Id);
        return ResponseEntity.status(200).body("Pillar Deleted");
    }
}
