package com.innovation.workplan.ServiceImplementations;

import com.innovation.workplan.CollectionModels.AccountingRatios;
import com.innovation.workplan.CollectionModels.Division;
import com.innovation.workplan.CollectionModels.RatioVariable;
import com.innovation.workplan.CollectionModels.RatiosVariablesRequest;
import com.innovation.workplan.Repositories.AccountingRatiosRepository;
import com.innovation.workplan.Services.AccountingRatiosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

@Service

public class AccountingRatiosServiceImpl implements AccountingRatiosService {

    @Autowired
    private AccountingRatiosRepository accountingRatiosRepository;
    @Autowired
    MongoTemplate mongoTemplate;

    Dictionary<String, Integer> dictionery= new Hashtable<>();

    @Autowired
    SequenceGeneratorService sequenceGenerator;
    @Override
    public ResponseEntity<String> saveRatio(AccountingRatios ratio) {
        Query query = new Query();
        query.addCriteria(Criteria.where("ratioName").is(ratio.getRatioName()));
        if (mongoTemplate.exists(query, "accounting_ratios")){
            return ResponseEntity.status(400).body("Accounting Ratio already exists!");
        }
        else {
            ratio.setId(sequenceGenerator.generateSequence(AccountingRatios.SEQUENCE_NAME));
            accountingRatiosRepository.save(ratio);
            return ResponseEntity.status(200).body("Ratio Saved");
        }
    }



    @Override
    public ResponseEntity<String> updateRatio(AccountingRatios ratio) {
        Query query1 = new Query();
        query1.addCriteria(Criteria.where("id").is(ratio.getId()));
        Update updateDef = new Update();
        updateDef.set("program", ratio.getProgram());
        updateDef.set("ratioName", ratio.getRatioName());
        updateDef.set("increamental_decreamental", ratio.getIncreamental_decreamental());
        updateDef.set("measurement_unit", ratio.getMeasurement_unit());
        mongoTemplate.findAndModify(query1, updateDef, new FindAndModifyOptions().returnNew(true), AccountingRatios.class);
        return ResponseEntity.status(200).body("ratio Updated");
    }

    @Override
    public ResponseEntity<String> delete(Long id) {
        accountingRatiosRepository.deleteById(id);
        return ResponseEntity.status(200).body("ratio Deleted");
    }

    @Override
    public AccountingRatios getRatio(Long id) {
        AccountingRatios ratio=accountingRatiosRepository.findById(id).orElse(null);
        if(ratio!=null){
            return  ratio;
        }
        return new AccountingRatios();
    }

    @Override
    public ResponseEntity<List<AccountingRatios>> getAllratios() {
        return ResponseEntity.status(200).body(accountingRatiosRepository.findAll());
    }


}
