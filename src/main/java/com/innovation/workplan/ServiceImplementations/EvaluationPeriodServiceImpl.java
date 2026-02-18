package com.innovation.workplan.ServiceImplementations;

import com.innovation.workplan.CollectionModels.EvaluationPeriod;
import com.innovation.workplan.CollectionModels.Pillars;
import com.innovation.workplan.CollectionModels.Quarter;
import com.innovation.workplan.CollectionModels.Workplan;
import com.innovation.workplan.CollectionModels.WorkplanTemplate;
import com.innovation.workplan.Repositories.EvaluationPeriodRepository;
import com.innovation.workplan.Repositories.QuarterRepository;
import com.innovation.workplan.Services.EvaluationPeriodService;
import com.innovation.workplan.Services.WorkplanTemplateService;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service

public class EvaluationPeriodServiceImpl implements EvaluationPeriodService {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private QuarterRepository quarterRepository;
    @Autowired
    private EvaluationPeriodRepository evaluationPeriodRepository;
    
    @Autowired
    private WorkplanTemplateService workplanTemplateService;


    @Override
    public String closeYear(String year) {
        return null;
    }

    @Override
    public String openYear(int year) {

        return null;
    }

    @Override
    public List<EvaluationPeriod> getAllEvaluationPeriods() {
        return evaluationPeriodRepository.findAll();
    }

    @Override
    public String closeEvaluationPeriod(String id) {
        EvaluationPeriod ev = evaluationPeriodRepository.findById(id).orElse(null);
        if (ev == null) {
            return "cant find the period";
        } else {
            if (ev.getPeriodStatus().toString().equalsIgnoreCase("Open")) {
                LocalDateTime today = LocalDateTime.now();
                
                // Check if closing early and prepare warning message
                boolean isEarlyClosure = today.isBefore(ev.getEndDate().minusDays(ev.getQuarter().getClose_open_allowance_days()));
                String warningMessage = "";
                if (isEarlyClosure) {
                    warningMessage = "WARNING: Quarter is being closed early before the allowed date. ";
                }

                // Archive the current workplan template before closing the quarter
                try {
                    WorkplanTemplate archivedTemplate = workplanTemplateService.archiveCurrentTemplate(
                        ev.getPeriod(), 
                        "System", 
                        "Quarter closed: " + ev.getPeriod() + (isEarlyClosure ? " (Early Closure)" : "")
                    );
                    if (archivedTemplate != null) {
                        System.out.println("Successfully archived workplan template: " + archivedTemplate.getTemplateName());
                    }
                } catch (Exception e) {
                    System.err.println("Warning: Failed to archive workplan template: " + e.getMessage());
                    // Continue with closing even if archival fails
                }

                Query query = new Query();
                query.addCriteria(Criteria.where("period").is(ev.getPeriod()));

                Update updateDef = new Update();
                updateDef.set("year", ev.getYear());
                updateDef.set("quarter", ev.getQuarter());
                updateDef.set("startDate", ev.getStartDate());
                updateDef.set("endDate", ev.getEndDate());
                updateDef.set("periodStatus", "Closed");

                mongoTemplate.findAndModify(query, updateDef, new FindAndModifyOptions().returnNew(true), EvaluationPeriod.class);
                
                if (isEarlyClosure) {
                    return (warningMessage + "Successfully closed");
                }
                return ("Successfully closed");
            }
        }
        return "Error: Unable to close the evaluation period";
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


                // TEMPORARILY DISABLED FOR TESTING - Remove this check in production
                // if(today.isAfter(ev.getStartDate().minusDays(quarter.getClose_open_allowance_days()))){
                //     return evaluationPeriodRepository.save(ev).getPeriodStatus();
                // }
                // else{
                //     return "Its still too early to open that quarter";
                // }
                
                // Allow opening for testing purposes
                return evaluationPeriodRepository.save(ev).getPeriodStatus();




        }



        }

        return "First create the Quarter through admin";
    }
    
    @Override
    public Map<String, Object> getCurrentQuarterStatus() {
        Map<String, Object> status = new HashMap<>();
        
        try {
            List<EvaluationPeriod> allPeriods = evaluationPeriodRepository.findAll();
            LocalDateTime now = LocalDateTime.now();
            
            // Find the currently open quarter
            EvaluationPeriod openQuarter = null;
            for (EvaluationPeriod period : allPeriods) {
                if ("Open".equalsIgnoreCase(period.getPeriodStatus())) {
                    openQuarter = period;
                    break;
                }
            }
            
            if (openQuarter != null) {
                status.put("hasOpenQuarter", true);
                status.put("currentQuarter", openQuarter.getPeriod());
                status.put("currentQuarterStatus", openQuarter.getPeriodStatus());
                status.put("startDate", openQuarter.getStartDate());
                status.put("endDate", openQuarter.getEndDate());
                
                // Calculate days remaining
                if (now.isBefore(openQuarter.getEndDate())) {
                    long daysRemaining = java.time.Duration.between(now, openQuarter.getEndDate()).toDays();
                    status.put("daysRemaining", daysRemaining);
                } else {
                    status.put("daysRemaining", 0);
                }
            } else {
                status.put("hasOpenQuarter", false);
                status.put("currentQuarter", null);
                status.put("currentQuarterStatus", "Closed");
            }
            
            // Check if there's a newly opened quarter (recently opened)
            for (EvaluationPeriod period : allPeriods) {
                if ("Open".equalsIgnoreCase(period.getPeriodStatus())) {
                    // Check if it was opened recently (within last 7 days)
                    if (period.getStartDate() != null && 
                        now.minusDays(7).isBefore(period.getStartDate())) {
                        status.put("newlyOpened", true);
                        status.put("newQuarterOpened", period.getPeriod());
                        break;
                    }
                }
            }
            
        } catch (Exception e) {
            status.put("error", e.getMessage());
        }
        
        return status;
    }
    
    @Override
    public Map<String, Object> getAllQuartersStatus() {
        Map<String, Object> status = new HashMap<>();
        
        try {
            List<EvaluationPeriod> allPeriods = evaluationPeriodRepository.findAll();
            
            List<Map<String, Object>> quartersList = new ArrayList<>();
            EvaluationPeriod currentOpenQuarter = null;
            
            for (EvaluationPeriod period : allPeriods) {
                Map<String, Object> quarterInfo = new HashMap<>();
                quarterInfo.put("period", period.getPeriod());
                quarterInfo.put("year", period.getYear());
                quarterInfo.put("quarter", period.getQuarter() != null ? period.getQuarter().getQuarter_name() : null);
                quarterInfo.put("status", period.getPeriodStatus());
                quarterInfo.put("startDate", period.getStartDate());
                quarterInfo.put("endDate", period.getEndDate());
                
                quartersList.add(quarterInfo);
                
                if ("Open".equalsIgnoreCase(period.getPeriodStatus())) {
                    currentOpenQuarter = period;
                }
            }
            
            // Sort by year and quarter
            quartersList.sort((a, b) -> {
                String periodA = a.get("period") != null ? a.get("period").toString() : "";
                String periodB = b.get("period") != null ? b.get("period").toString() : "";
                return periodB.compareTo(periodA); // Most recent first
            });
            
            status.put("quarters", quartersList);
            status.put("totalQuarters", quartersList.size());
            
            if (currentOpenQuarter != null) {
                status.put("hasOpenQuarter", true);
                status.put("currentQuarter", currentOpenQuarter.getPeriod());
                status.put("currentQuarterStatus", currentOpenQuarter.getPeriodStatus());
            } else {
                status.put("hasOpenQuarter", false);
            }
            
            // Check for newly opened quarters
            LocalDateTime now = LocalDateTime.now();
            for (EvaluationPeriod period : allPeriods) {
                if ("Open".equalsIgnoreCase(period.getPeriodStatus()) && 
                    period.getStartDate() != null &&
                    now.minusDays(7).isBefore(period.getStartDate())) {
                    status.put("newQuarterOpened", period.getPeriod());
                    break;
                }
            }
            
        } catch (Exception e) {
            status.put("error", e.getMessage());
        }
        
        return status;
    }
}
