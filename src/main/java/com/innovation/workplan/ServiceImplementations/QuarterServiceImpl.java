package com.innovation.workplan.ServiceImplementations;

import com.innovation.workplan.CollectionModels.Pillars;
import com.innovation.workplan.CollectionModels.Quarter;
import com.innovation.workplan.Repositories.QuarterRepository;
import com.innovation.workplan.Services.QuarterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Arrays;
import java.util.List;

@Service

public class QuarterServiceImpl implements QuarterService {

    @Autowired
    private QuarterRepository quarterRepository;
    @Autowired
    private MongoTemplate mongoTemplate;

    final  private List<String> quarter_names = Arrays.asList("Q1","Q2","Q3","Q4");




    @Override
    public String saveQuarter(Quarter quarter) {
        String name=quarter.getQuarter_name().toUpperCase();
        quarter.setQuarter_name(name);
        if(quarter_names.contains(name)) {
            Quarter q = quarterRepository.findById(name).orElse(null);
            if (q == null) {

                    quarterRepository.save(quarter);
                    return "successfully saved";
                }

                    updateQuarter(quarter);
                    return "successfully updated existing quarter";

            }
        return "Incorrect name for a Quarter";
        }

    @Override
    public String updateQuarter(Quarter quarter) {
        Query query = new Query();
        query.addCriteria(Criteria.where("quarter_name").is(quarter.getQuarter_name()));
        Update updateDef = new Update();
        updateDef.set("close_open_allowance_days", quarter.getClose_open_allowance_days());
        mongoTemplate.findAndModify(query, updateDef, new FindAndModifyOptions().returnNew(true), Quarter.class);
        return ("Successfully Updated");
    }

    @Override
    public List<Quarter> getAllQuarters() {
        return quarterRepository.findAll();
    }
}
