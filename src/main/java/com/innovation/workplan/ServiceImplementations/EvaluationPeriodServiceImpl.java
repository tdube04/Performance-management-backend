package com.innovation.workplan.ServiceImplementations;

import com.innovation.workplan.CollectionModels.EvaluationPeriod;
import com.innovation.workplan.CollectionModels.Pillars;
import com.innovation.workplan.CollectionModels.Quarter;
import com.innovation.workplan.CollectionModels.Workplan;
import com.innovation.workplan.Repositories.EvaluationPeriodRepository;
import com.innovation.workplan.Repositories.QuarterRepository;
import com.innovation.workplan.Services.EvaluationPeriodService;
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

@Service

public class EvaluationPeriodServiceImpl implements EvaluationPeriodService {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private QuarterRepository quarterRepository;
    @Autowired
    private EvaluationPeriodRepository evaluationPeriodRepository;


    @Override
    public String closeYear(String year) {
        return null;
    }

    @Override
    public String openYear(int year) {

        return null;
    }

    @Override
    public String closeEvaluationPeriod(String id) {
        EvaluationPeriod ev = evaluationPeriodRepository.findById(id).orElse(null);
        if (ev == null) {
            return "cant find the period";
        } else {
            if (ev.getPeriodStatus().toString().equalsIgnoreCase("Open")) {
                LocalDateTime today =
                        LocalDateTime.now();
                if (today.isAfter(ev.getEndDate().minusDays(ev.getQuarter().getClose_open_allowance_days()))) {

                    Query query = new Query();
                    query.addCriteria(Criteria.where("period").is(ev.getPeriod()));

                    Update updateDef = new Update();
                    updateDef.set("year", ev.getYear());
                    updateDef.set("quarter", ev.getQuarter());
                    updateDef.set("startDate", ev.getStartDate());
                    updateDef.set("endDate", ev.getEndDate());
                    updateDef.set("periodStatus", "Closed");


                    mongoTemplate.findAndModify(query, updateDef, new FindAndModifyOptions().returnNew(true), EvaluationPeriod.class);
                    return ("Successfully closed");
                }
            }
        }
        return "You can only close the quarter "+"\t"+ev.getQuarter().getClose_open_allowance_days()+"\t"+"days before ending";
    }



    @Override
    public String openEvaluationPeriod(int year, String quarter_name) {
        LocalDateTime Q1_start =
                LocalDateTime.of(year, Month.JANUARY, 1, 00, 00, 01);
        LocalDateTime Q1_end =Q1_start.plusMonths(3);
        LocalDateTime Q2_start =
                Q1_end.plusDays(1);
        LocalDateTime Q2_end =Q2_start.plusMonths(3);
         LocalDateTime Q3_start =
                Q2_end.plusDays(1);
         LocalDateTime Q3_end =Q3_start.plusMonths(3);
        LocalDateTime Q4_start =
                Q3_end.plusDays(1);
        LocalDateTime Q4_end =Q4_start.plusMonths(3);
        Quarter quarter=quarterRepository.findById(quarter_name).orElse(null);
        if(quarter!=null|!quarter.equals(null)){
            EvaluationPeriod ev=new EvaluationPeriod();
            String id=year+"-"+quarter_name;
            ev.setPeriod(id);
            ev.setQuarter(quarter);
            ev.setPeriodStatus("Open");
            if(quarter_name.equalsIgnoreCase("Q1")){
                ev.setStartDate(Q1_start);
                ev.setEndDate(Q1_end);

            }
            else if (quarter_name.equalsIgnoreCase("Q2")) {
                ev.setStartDate(Q2_start);
                ev.setEndDate(Q2_end);

            }
            else if (quarter_name.equalsIgnoreCase("Q3")) {
                ev.setStartDate(Q3_start);
                ev.setEndDate(Q3_end);

            }
            else if (quarter_name.equalsIgnoreCase("Q4")) {
                ev.setStartDate(Q4_start);
                ev.setEndDate(Q4_end);

            }
            else {
                return "Incorrect Quarter name";
            }


            Query duplicate = new Query();
            duplicate.addCriteria(Criteria.where("_id").is(id));


            if (mongoTemplate.exists(duplicate, "EvaluationPeriods_tbl")) {
                EvaluationPeriod ev1=evaluationPeriodRepository.findById(id).orElse(null);
                if(ev1.getPeriodStatus().equalsIgnoreCase("Closed")){
                    Query query = new Query();
                    query.addCriteria(Criteria.where("_id").is(id));

                    Update updateDef = new Update();
                    updateDef.set("period", ev.getPeriod());
                    updateDef.set("year", ev.getYear());
                    updateDef.set("quarter", ev.getQuarter());
                    updateDef.set("startDate", ev.getStartDate());
                    updateDef.set("endDate", ev.getEndDate());
                    updateDef.set("periodStatus","Open");


                    mongoTemplate.findAndModify(query, updateDef, new FindAndModifyOptions().returnNew(true), EvaluationPeriod.class);
                    return ("Successfully Open Existing period");

                }
                return "unable to open";}
            else {

                ev.setPeriod(id);
                ev.setQuarter(new Quarter(quarter.getQuarter_name(),quarter.getClose_open_allowance_days()));

                ev.setYear(year);
                ev.setPeriodStatus("open");
                LocalDateTime today =
                        LocalDateTime.now();


                if(today.isAfter(ev.getStartDate().minusDays(quarter.getClose_open_allowance_days()))){
                    return evaluationPeriodRepository.save(ev).getPeriodStatus();
                }
                else{
                    return "Its still too early to open that quarter";
                }




        }



        }

        return "First create the Quarter through admin";
    }
}
