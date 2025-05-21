package com.innovation.workplan.ServiceImplementations;

import com.innovation.workplan.CollectionModels.Division;
import com.innovation.workplan.CollectionModels.MeasurementUnits;
import com.innovation.workplan.Repositories.MeasurementUnitsRepository;
import com.innovation.workplan.Services.MeasurementUnitsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
@Service

public class MeasurementUnitsServiceServiceImpl implements MeasurementUnitsService {
    @Autowired
    MongoTemplate mongoTemplate;
    @Autowired
    MeasurementUnitsRepository measurementUnitsRepository;
    @Override
    public String saveMeasurementUnits(MeasurementUnits measurementUnit) {
        return  measurementUnitsRepository.save(measurementUnit).getShortCode();
    }

    @Override
    public List<MeasurementUnits> getAllMeasurementUnits() {
        List<MeasurementUnits> m=new ArrayList<>();
        List<MeasurementUnits> measurementUnits=measurementUnitsRepository.findAll();
        if(measurementUnits.isEmpty()==false) {
            for (MeasurementUnits a : measurementUnits) {
                if (a.getActive() == true) {
                    m.add(a);
                }
            }
        }
        return m;
    }

    @Override
    public MeasurementUnits updateMeasurementUnits(MeasurementUnits measurementUnit) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(measurementUnit.getUnitName()));
        Update updateDef = new Update();

        updateDef.set("shortCode", measurementUnit.getShortCode());
        updateDef.set("active", measurementUnit.getActive());

        return mongoTemplate.findAndModify(query, updateDef, new FindAndModifyOptions().returnNew(true), MeasurementUnits.class);
    }



    @Override
    public void deleteMeasurementUnits(String Id) {
        Boolean new_state=false;
        MeasurementUnits mu=measurementUnitsRepository.findById(Id).orElse(null);
        if(mu!=null) {
            Query query = new Query();
            query.addCriteria(Criteria.where("_id").is(Id));
            Update updateDef = new Update();

            updateDef.set("shortCode", mu.getShortCode());
            updateDef.set("active", new_state);

            mongoTemplate.findAndModify(query, updateDef, new FindAndModifyOptions().returnNew(true), MeasurementUnits.class);
        }

    }

    @Override
    public void permanentlyDeleteMeasurementUnits(String Id) {
        measurementUnitsRepository.deleteById(Id);

    }

    @Override
    public MeasurementUnits findMeasurementUnitById(String Id) {
         MeasurementUnits m=measurementUnitsRepository.findById(Id).orElse(null);
         if(m!=null){
             if(m.getActive()==false){
                 return null;
             }
             return m;
         }

        return null;
    }
}
