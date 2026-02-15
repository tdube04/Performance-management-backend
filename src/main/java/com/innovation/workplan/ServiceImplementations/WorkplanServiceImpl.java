package com.innovation.workplan.ServiceImplementations;


import com.innovation.workplan.CollectionModels.*;
import com.innovation.workplan.CollectionModels.KPI;
import  com.innovation.workplan.CollectionModels.MPI;
import com.innovation.workplan.Configuration.SecurityUtils;
import com.innovation.workplan.Repositories.UserEntityRepository;
import com.innovation.workplan.Repositories.WorkPlanRepository;
import com.innovation.workplan.Services.EmailService;
import com.innovation.workplan.Services.ScorecardService;
import com.innovation.workplan.Services.UserEntityService;
import com.innovation.workplan.Services.WorkPlanService;

import java.security.Principal;

import com.innovation.workplan.Utilities.JWTUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;

@Service

public class WorkplanServiceImpl implements WorkPlanService {

    @Autowired
    private WorkPlanRepository workPlanRepository;

    public final String domain = "@zimra.co.zw";
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired

    private EmailService emailService;

    @Autowired

    private ScorecardService scorecardService;
    @Autowired
    HttpServletRequest httpServletRequest;
    @Autowired
    JWTUtility jwtUtility;

    private KPI kpi;

    private MPI mpi;
    @Autowired

    private UserEntityService userEntityService;

    @Autowired
    SequenceGeneratorService sequenceGenerator;

    @Autowired
    UserEntityRepository userEntityRepository;

    @Override
    public long save(Workplan workplan) {
        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(workplan.getId()));

        Query duplicate = new Query();
        duplicate.addCriteria(Criteria.where("user_email").is(workplan.getUser_email()));
        duplicate.addCriteria(Criteria.where("evaluationPeriod").is(workplan.getEvaluationPeriod()));
        duplicate.addCriteria(Criteria.where("workplanStatus").is(workplan.getWorkplanStatus()));

        if (mongoTemplate.exists(query, "workplans_tbl")) {
            return 9000l;
        } else if (mongoTemplate.exists(duplicate, "workplans_tbl")) {
            return 9001l;

        } else {
            workplan.setId(sequenceGenerator.generateSequence(Workplan.SEQUENCE_NAME));
            return workPlanRepository.save(workplan).getId();
        }
    }

    @Override
    public Page<Workplan> searchPlan(Long id, String name, String period, Pageable pageable) {
        Query query = new Query().with(pageable);
        List<Criteria> criteria = new ArrayList<>();
        if (id != null) {
            criteria.add(Criteria.where("id").is(id));
        }
        if (period != null && !period.isEmpty()) {
            criteria.add(Criteria.where("evaluationPeriod").is(period));
        }

        if (name != null) {
            criteria.add(Criteria.where("user_email").is(name));
        }


//        if(name!=null && !name.isEmpty()){
//            criteria.add(Criteria.where("user_email" +
//                    "").regex(name,"i"));
//        }

        if (!criteria.isEmpty()) {
            query.addCriteria(new Criteria().andOperator(criteria.toArray(new Criteria[0])));
        }
        Page<Workplan> plans = PageableExecutionUtils.getPage(mongoTemplate.find(query, Workplan.class),
                pageable, () -> mongoTemplate
                        .count(query.skip(0).limit(0), Workplan.class));
        return plans;
    }

    @Override
    public Workplan searchUserPlan(String User_email, String period, String planStatus) {
        Query query = new Query();
        List<Criteria> criteria = new ArrayList<>();
        if (User_email != null && !period.isEmpty()) {
            criteria.add(Criteria.where("user_email").is(User_email));
        }
        if (period != null && !period.isEmpty()) {
            criteria.add(Criteria.where("evaluationPeriod").is(period));
        }


        if (planStatus != null && !planStatus.isEmpty()) {
            criteria.add(Criteria.where("workplanStatus").is(planStatus));
        }
        if (!criteria.isEmpty()) {
            query.addCriteria(new Criteria().andOperator(criteria.toArray(new Criteria[0])));
        }
        Workplan workplan = mongoTemplate.findOne(query, Workplan.class);
        return workplan;
    }

    @Override
    public List<Workplan> searchPlanByEvaluator(String evaluator_email, String period, String planStatus) {
        Query query = new Query();
        List<Criteria> criteria = new ArrayList<>();
        if (evaluator_email != null) {
            criteria.add(Criteria.where("evaluator_email").is(evaluator_email));
        }
        if (period != null && !period.isEmpty()) {
            criteria.add(Criteria.where("evaluationPeriod").is(period));
        }


        if (planStatus != null && !planStatus.isEmpty()) {
            criteria.add(Criteria.where("workplanStatus").is(planStatus));
        }
        if (!criteria.isEmpty()) {
            query.addCriteria(new Criteria().andOperator(criteria.toArray(new Criteria[0])));
        }
        List<Workplan> workplans = mongoTemplate.find(query, Workplan.class);
        return workplans;
    }

    @Override
    public List<Workplan> searchPlanByStatus(String planStatus) {
        Query query = new Query();
        List<Criteria> criteria = new ArrayList<>();
        if (planStatus != null && !planStatus.isEmpty()) {
            criteria.add(Criteria.where("workplanStatus").is(planStatus));
        }
        if (!criteria.isEmpty()) {
            query.addCriteria(new Criteria().andOperator(criteria.toArray(new Criteria[0])));
        }
        List<Workplan> workplans = mongoTemplate.find(query, Workplan.class);
        return workplans;
    }


    @Override
    public Workplan getMyWorkPlan(String period) {
        Query query = new Query();
        List<Criteria> criteria = new ArrayList<>();
        if (SecurityUtils.getCurrentUserLogin() != null) {
            criteria.add(Criteria.where("user_email").is(SecurityUtils.getCurrentUserLogin()));
        }
        if (period != null && !period.isEmpty()) {
            criteria.add(Criteria.where("evaluationPeriod").is(period));
        }


        if (!criteria.isEmpty()) {
            query.addCriteria(new Criteria().andOperator(criteria.toArray(new Criteria[0])));
        }
        Workplan workplan = mongoTemplate.findOne(query, Workplan.class);
        return workplan;
    }
    public String approveCGWorkplan(Long id) {
        String authorization = httpServletRequest.getHeader("Authorization");
        String token = null;
        String userName = null;
        float a;
        float total;
        float b;
        float weight1;


        token = authorization.substring(7);
        userName = jwtUtility.extractUsername(token);
        Workplan plan=workPlanRepository.findById(id).orElse(null);

        if(plan!=null) {
            if(Integer.parseInt(userEntityRepository.findById(plan.getUser_email()).orElse(null).getGrade())==1
                    && Integer.parseInt(userEntityRepository.findById(plan.getAppraiser_email()).orElse(null).getGrade())==0){
            if (userName.toString().equalsIgnoreCase(plan.getAppraiser_email().toString()) &&
                    plan.getWorkplanStatus().equalsIgnoreCase("PendingApproval")) {
                Query query = new Query();
                query.addCriteria(Criteria.where("_id").is(plan.getId()));
                Update updateworkplan = new Update();
                updateworkplan.set("user_email", plan.getUser_email());
                updateworkplan.set("evaluator_email", plan.getEvaluator_email());
                updateworkplan.set("appraiser_email", SecurityUtils.getCurrentUserLogin().get().toString());
                updateworkplan.set("evaluationPeriod", plan.getEvaluationPeriod());

                updateworkplan.set("AreasOfPerformance", plan.getAreasOfPerformance());
                updateworkplan.set("workplanStatus", "Approved");
                updateworkplan.set("statusComments", plan.getStatusComments());
                updateworkplan.set("dateApproved", LocalDateTime.now());
                mongoTemplate.findAndModify(query, updateworkplan, new FindAndModifyOptions().returnNew(true), Workplan.class);

                emailService.sendSimpleMessage(plan.getUser_email()+domain, "Workplan Approved ", "Good day your workplan has been approved :"+ "\t" +
                        " by"+"\n"+ SecurityUtils.getCurrentUserLogin().toString()+"\n"+"at:"+ LocalDateTime.now());
                createScorecard(plan);
                Workplan wp1 = workPlanRepository.findById(plan.getId()).orElse(null);
                if (wp1 != null) {
                    UserEntity appraiser = userEntityRepository.findById(plan.getUser_email()).orElse(null);
                    List<String> appraisees = appraiser.getAppraisees();
                    if (wp1.getWorkplanStatus().equalsIgnoreCase("Approved") | wp1.getWorkplanStatus().equalsIgnoreCase("Ammended")) {
                        if(appraisees!=null){  if (appraisees.size() == 0 |appraisees==null) {
                            return "Successfully approved";
                        }
                        else {
                            for (String user : appraisees) {
                                Dictionary<String, Integer> dictionary=sortWeights(plan,user);
                                Workplan wp = new Workplan();
                                if (wp1.getWorkplanStatus().equalsIgnoreCase("Ammended")) {
                                    wp.setWorkplanStatus("PendingAmmendApproval");
                                }
                                wp.setWorkplanStatus("Incomplete");
                                wp.setAppraiser_email(wp1.getUser_email());
                                wp.setEvaluationPeriod(wp1.getEvaluationPeriod());
                                wp.setUser_email(user);
                                wp.setEvaluator_email(plan.getAppraiser_email());

                                List<WorkplanPerformanceArea> areas = new ArrayList<>();
                                for (WorkplanPerformanceArea wpa : wp1.getAreasOfPerformance()) {

                                    total= dictionary.get(wpa.getPerformanceArea());
                                    WorkplanPerformanceArea area = new WorkplanPerformanceArea();
                                    String area_name=wpa.getPerformanceArea();
                                    ArrayList<KPI> newprogs = new ArrayList<>();
                                    ArrayList<KPI> programs = wpa.getPrograms();


                                    for (KPI prog : programs) {
                                        ArrayList<MPI> newIndicators = new ArrayList<>();
                                        List<MPI> indicators = prog.getIndicators();


                                        for (int i = 0; i < indicators.size(); i++) {


                                            a=indicators.get(i).getWeight();
                                            b=wpa.getWeight();

                                            for (ResponsiblePerson person : indicators.get(i).getResponsibleResources()) {
                                                if (person.getUsername().equalsIgnoreCase(user)) {
                                                    KPI kpi = new KPI();
//                                                    newIndicators.add(indicators.get(i));
                                                    kpi.setName(indicators.get(i).getDescription());
                                                    kpi.setIndicators(new ArrayList<>());
//                                                    int final_weight=Math.round(weight1);

                                                    weight1=(a*b/total);

                                                    kpi.setWeight(Math.round(weight1));
//                                                    kpi.setWeight(Math.round(weight1));
//                                                    kpi.setWeight(weight1);
//                                                    total_weight=total_weight+indicators.get(i).getWeight();
                                                    kpi.setContributedPillar(prog.getContributedPillar());
                                                    if(kpi!=null){
                                                        newprogs.add(kpi);}
                                                }
                                            }
                                        }
//                                        dictionery.put(area_name,total_weight);
                                    }
                                    area.setPrograms(newprogs);
                                    area.setPerformanceArea(wpa.getPerformanceArea());
                                    area.setWeight(wpa.getWeight());
                                    area.setSection(wpa.getSection());
                                    area.setDescription(wpa.getDescription());
                                    areas.add(area);
                                }
                                wp.setAreasOfPerformance(areas);
                                List<WorkplanPerformanceArea> wp_areas = wp.getAreasOfPerformance();
                                for (int w = 0; w < wp_areas.size(); w++) {
                                    List<KPI> wp_programs = wp_areas.get(w).getPrograms();
                                    for (int j = 0; j < wp_programs.size(); j++) {
                                        if(wp_programs.get(j)!=null){
                                            if (wp_programs.get(j).getIndicators()==null) {
                                                wp.getAreasOfPerformance().remove(wp_areas.get(w));
                                            }}
                                    }
                                }
                                if (wp.getAreasOfPerformance().size() > 0) {
                                    save(wp);
                                    emailService.sendSimpleMessage(wp.getUser_email()+domain, "New Workplan ", "Good day you have been assigned  new workplan :"+ "\t" +
                                            " by"+"\n"+ wp.getAppraiser_email()+"\n"+"at:"+ LocalDateTime.now());
                                }
                            }
                        }}


                    }

                    return ("Workplan for \t" + plan.getUser_email() + "\n Successfully approved");
                }
            }
        }
        }
        return "Can not find workplan";
    }


    @Override
    public String approveWorkplan(Long id) {
        String authorization = httpServletRequest.getHeader("Authorization");
        String token = null;
        String userName = null;
        float a;
        float total;
        float b;
        float weight1;


            token = authorization.substring(7);
            userName = jwtUtility.extractUsername(token);
        Workplan plan=workPlanRepository.findById(id).orElse(null);
        if(plan!=null) {
            if(Integer.parseInt(userEntityRepository.findById(plan.getUser_email()).orElse(null).getGrade())==1
                    && Integer.parseInt(userEntityRepository.findById(plan.getAppraiser_email()).orElse(null).getGrade())==0){
                if (userName.toString().equalsIgnoreCase(plan.getAppraiser_email().toString()) &&
                        plan.getWorkplanStatus().equalsIgnoreCase("PendingApproval")) {
                    Query query = new Query();
                    query.addCriteria(Criteria.where("_id").is(plan.getId()));
                    Update updateworkplan = new Update();
                    updateworkplan.set("user_email", plan.getUser_email());
                    updateworkplan.set("evaluator_email", plan.getEvaluator_email());
                    updateworkplan.set("appraiser_email", SecurityUtils.getCurrentUserLogin().get().toString());
                    updateworkplan.set("evaluationPeriod", plan.getEvaluationPeriod());

                    updateworkplan.set("AreasOfPerformance", plan.getAreasOfPerformance());
                    updateworkplan.set("workplanStatus", "Approved");
                    updateworkplan.set("statusComments", plan.getStatusComments());
                    updateworkplan.set("dateApproved", LocalDateTime.now());
                    mongoTemplate.findAndModify(query, updateworkplan, new FindAndModifyOptions().returnNew(true), Workplan.class);

                    // Send email but don't block scorecard creation if email fails
                    try {
                        emailService.sendSimpleMessage(plan.getUser_email()+domain, "Workplan Approved ", "Good day your workplan has been approved :"+ "\t" +
                                " by"+"\n"+ SecurityUtils.getCurrentUserLogin().toString()+"\n"+"at:"+ LocalDateTime.now());
                    } catch (Exception e) {
                        System.out.println("WARNING: Failed to send email notification: " + e.getMessage());
                        e.printStackTrace();
                    }
                    
                    createScorecard(plan);
                    Workplan wp1 = workPlanRepository.findById(plan.getId()).orElse(null);
                    if (wp1 != null) {
                        UserEntity appraiser = userEntityRepository.findById(plan.getUser_email()).orElse(null);
                        List<String> appraisees = appraiser.getAppraisees();
                        if (wp1.getWorkplanStatus().equalsIgnoreCase("Approved") | wp1.getWorkplanStatus().equalsIgnoreCase("Ammended")) {
                            if(appraisees!=null){  if (appraisees.size() == 0 |appraisees==null) {
                                return "Successfully approved";
                            }
                            else {
                                for (String user : appraisees) {
                                    Dictionary<String, Integer> dictionary=sortWeights(plan,user);
                                    Workplan wp = new Workplan();
                                    if (wp1.getWorkplanStatus().equalsIgnoreCase("Ammended")) {
                                        wp.setWorkplanStatus("PendingAmmendApproval");
                                    }
                                    wp.setEvaluator_email(plan.getAppraiser_email());
                                    wp.setWorkplanStatus("Incomplete");
                                    wp.setAppraiser_email(wp1.getUser_email());
                                    wp.setEvaluationPeriod(wp1.getEvaluationPeriod());
                                    wp.setUser_email(user);

                                    List<WorkplanPerformanceArea> areas = new ArrayList<>();
                                    for (WorkplanPerformanceArea wpa : wp1.getAreasOfPerformance()) {

                                        total= dictionary.get(wpa.getPerformanceArea());
                                        WorkplanPerformanceArea area = new WorkplanPerformanceArea();
                                        String area_name=wpa.getPerformanceArea();
                                        ArrayList<KPI> newprogs = new ArrayList<>();
                                        ArrayList<KPI> programs = wpa.getPrograms();


                                        for (KPI prog : programs) {
                                            ArrayList<MPI> newIndicators = new ArrayList<>();
                                            List<MPI> indicators = prog.getIndicators();


                                            for (int i = 0; i < indicators.size(); i++) {


                                                a=indicators.get(i).getWeight();
                                                b=wpa.getWeight();

                                                for (ResponsiblePerson person : indicators.get(i).getResponsibleResources()) {
                                                    if (person.getUsername().equalsIgnoreCase(user)) {
                                                        KPI kpi = new KPI();
//                                                    newIndicators.add(indicators.get(i));
                                                        kpi.setName(indicators.get(i).getDescription());
                                                        kpi.setIndicators(new ArrayList<>());
//                                                    int final_weight=Math.round(weight1);

                                                        weight1=(a*b/total);

                                                        kpi.setWeight(Math.round(weight1));
//                                                    kpi.setWeight(Math.round(weight1));
//                                                    kpi.setWeight(weight1);
//                                                    total_weight=total_weight+indicators.get(i).getWeight();
                                                        kpi.setContributedPillar(prog.getContributedPillar());
                                                        if(kpi!=null){
                                                            newprogs.add(kpi);}
                                                    }
                                                }
                                            }
//                                        dictionery.put(area_name,total_weight);
                                        }
                                        area.setPrograms(newprogs);
                                        area.setPerformanceArea(wpa.getPerformanceArea());
                                        area.setWeight(wpa.getWeight());
                                        area.setSection(wpa.getSection());
                                        area.setDescription(wpa.getDescription());
                                        areas.add(area);
                                    }
                                    wp.setAreasOfPerformance(areas);
                                    List<WorkplanPerformanceArea> wp_areas = wp.getAreasOfPerformance();
                                    for (int w = 0; w < wp_areas.size(); w++) {
                                        List<KPI> wp_programs = wp_areas.get(w).getPrograms();
                                        for (int j = 0; j < wp_programs.size(); j++) {
                                            if(wp_programs.get(j)!=null){
                                                if (wp_programs.get(j).getIndicators()==null) {
                                                    wp.getAreasOfPerformance().remove(wp_areas.get(w));
                                                }}
                                        }
                                    }
                                    if (wp.getAreasOfPerformance().size() > 0) {
                                        save(wp);
                                        emailService.sendSimpleMessage(wp.getUser_email()+domain, "New Workplan ", "Good day you have been assigned  new workplan :"+ "\t" +
                                                " by"+"\n"+ wp.getAppraiser_email()+"\n"+"at:"+ LocalDateTime.now());
                                    }
                                }
                            }}


                        }

                        return ("Workplan for \t" + plan.getUser_email() + "\n Successfully approved");
                    }
                }
            }

            if (userName.toString().equalsIgnoreCase(plan.getAppraiser_email().toString()) &&
                    plan.getWorkplanStatus().equalsIgnoreCase("PendingApproval")) {
                Query query = new Query();
                query.addCriteria(Criteria.where("_id").is(plan.getId()));
                Update updateworkplan = new Update();
                updateworkplan.set("user_email", plan.getUser_email());
                updateworkplan.set("evaluator_email", plan.getEvaluator_email());
                updateworkplan.set("appraiser_email", SecurityUtils.getCurrentUserLogin().get().toString());
                updateworkplan.set("evaluationPeriod", plan.getEvaluationPeriod());

                updateworkplan.set("AreasOfPerformance", plan.getAreasOfPerformance());
                updateworkplan.set("workplanStatus", "Approved");
                updateworkplan.set("statusComments", plan.getStatusComments());
                mongoTemplate.findAndModify(query, updateworkplan, new FindAndModifyOptions().returnNew(true), Workplan.class);

                // Send email but don't block scorecard creation if email fails
                try {
                    emailService.sendSimpleMessage(plan.getUser_email()+domain, "Workplan Approved ", "Good day your workplan has been approved :"+ "\t" +
                            " by"+"\n"+ SecurityUtils.getCurrentUserLogin().toString()+"\n"+"at:"+ LocalDateTime.now());
                } catch (Exception e) {
                    System.out.println("WARNING: Failed to send email notification at line 488: " + e.getMessage());
                    e.printStackTrace();
                }

                System.out.println("=== APPROVE WORKPLAN (NON-GRADE) DEBUG ===");
                System.out.println("About to call createScorecard for workplan ID: " + plan.getId());
                System.out.println("Workplan user_email: " + plan.getUser_email());
                createScorecard(plan);
                System.out.println("createScorecard completed");
                System.out.println("=== END APPROVE WORKPLAN (NON-GRADE) DEBUG ===");
                Workplan wp1 = workPlanRepository.findById(plan.getId()).orElse(null);
                if (wp1 != null) {
                    UserEntity appraiser = userEntityRepository.findById(plan.getUser_email()).orElse(null);
                    List<String> appraisees = appraiser.getAppraisees();
                    if (wp1.getWorkplanStatus().equalsIgnoreCase("Approved") | wp1.getWorkplanStatus().equalsIgnoreCase("Ammended")) {
                      if(appraisees!=null){  if (appraisees.size() == 0 |appraisees==null) {
                            return "Successfully approved";
                        }
                        else {
                            for (String user : appraisees) {
                                Dictionary<String, Integer> dictionary=sortWeights(plan,user);
                                Workplan wp = new Workplan();
                                if (wp1.getWorkplanStatus().equalsIgnoreCase("Ammended")) {
                                    wp.setWorkplanStatus("PendingAmmendApproval");
                                }
                                wp.setEvaluator_email(plan.getAppraiser_email());
                                wp.setWorkplanStatus("Incomplete");
                                wp.setAppraiser_email(wp1.getUser_email());
                                wp.setEvaluationPeriod(wp1.getEvaluationPeriod());
                                wp.setUser_email(user);

                                List<WorkplanPerformanceArea> areas = new ArrayList<>();
                                for (WorkplanPerformanceArea wpa : wp1.getAreasOfPerformance()) {

                                    total= dictionary.get(wpa.getPerformanceArea());
                                    WorkplanPerformanceArea area = new WorkplanPerformanceArea();
                                    String area_name=wpa.getPerformanceArea();
                                    ArrayList<KPI> newprogs = new ArrayList<>();
                                    ArrayList<KPI> programs = wpa.getPrograms();


                                    for (KPI prog : programs) {
                                        ArrayList<MPI> newIndicators = new ArrayList<>();
                                        List<MPI> indicators = prog.getIndicators();


                                        for (int i = 0; i < indicators.size(); i++) {


                                            a=indicators.get(i).getWeight();
                                            b=wpa.getWeight();

                                            for (ResponsiblePerson person : indicators.get(i).getResponsibleResources()) {
                                                if (person.getUsername().equalsIgnoreCase(user)) {
                                                    KPI kpi = new KPI();
//                                                    newIndicators.add(indicators.get(i));
                                                    kpi.setName(indicators.get(i).getDescription());
                                                    kpi.setIndicators(new ArrayList<>());
//                                                    int final_weight=Math.round(weight1);

                                                    weight1=(a*b/total);

                                                    kpi.setWeight(Math.round(weight1));
//                                                    kpi.setWeight(Math.round(weight1));
//                                                    kpi.setWeight(weight1);
//                                                    total_weight=total_weight+indicators.get(i).getWeight();
                                                    kpi.setContributedPillar(prog.getContributedPillar());
                                                    if(kpi!=null){
                                                        newprogs.add(kpi);}
                                                }
                                            }
                                        }
//                                        dictionery.put(area_name,total_weight);
                                    }
                                    area.setPrograms(newprogs);
                                    area.setPerformanceArea(wpa.getPerformanceArea());
                                    area.setWeight(wpa.getWeight());
                                    area.setSection(wpa.getSection());
                                    area.setDescription(wpa.getDescription());
                                    areas.add(area);
                                }
                                wp.setAreasOfPerformance(areas);
                                List<WorkplanPerformanceArea> wp_areas = wp.getAreasOfPerformance();
                                for (int w = 0; w < wp_areas.size(); w++) {
                                    List<KPI> wp_programs = wp_areas.get(w).getPrograms();
                                    for (int j = 0; j < wp_programs.size(); j++) {
                                        if(wp_programs.get(j)!=null){
                                        if (wp_programs.get(j).getIndicators()==null) {
                                            wp.getAreasOfPerformance().remove(wp_areas.get(w));
                                        }}
                                    }
                                }
                                if (wp.getAreasOfPerformance().size() > 0) {
                                    save(wp);
                                    try {
                                        emailService.sendSimpleMessage(wp.getUser_email()+domain, "New Workplan ", "Good day you have been assigned  new workplan :"+ "\t" +
                           " by"+"\n"+ wp.getAppraiser_email()+"\n"+"at:"+ LocalDateTime.now());
                                    } catch (Exception e) {
                                        System.out.println("WARNING: Failed to send new workplan email: " + e.getMessage());
                                    }
                                }
                            }
                        }}


                    }

                    return ("Workplan for \t" + plan.getUser_email() + "\n Successfully approved");
                }
            }
        }
        return "Can not find workplan";
    }

    public Dictionary<String, Integer> sortWeights(Workplan workplan, String user){
            List<WorkplanPerformanceArea> areas = new ArrayList<>();
            Dictionary<String, Integer> dictionery= new Hashtable<>();
            for (WorkplanPerformanceArea wpa : workplan.getAreasOfPerformance()) {
                WorkplanPerformanceArea area = new WorkplanPerformanceArea();
                String area_name=wpa.getPerformanceArea();
                ArrayList<KPI> newprogs = new ArrayList<>();
                ArrayList<KPI> programs = wpa.getPrograms();
                Integer total_weight=0;
                for (KPI prog : programs) {
                    ArrayList<MPI> newIndicators = new ArrayList<>();
                    List<MPI> indicators = prog.getIndicators();
                    for (int i = 0; i < indicators.size(); i++) {
                        for (ResponsiblePerson person : indicators.get(i).getResponsibleResources()) {
                            if (person.getUsername().equalsIgnoreCase(user)) {
                                KPI kpi = new KPI();

                                total_weight=total_weight+indicators.get(i).getWeight();
                                if(kpi!=null){
                                    newprogs.add(kpi);}
                            }
                        }
                    }
                }
                dictionery.put(area_name,total_weight);
            }
        return dictionery;
    }




//    public String OldapproveWorkplan(Long id) {
//        String authorization = httpServletRequest.getHeader("Authorization");
//        String token = null;
//        String userName = null;
//
//
//        token = authorization.substring(7);
//        userName = jwtUtility.extractUsername(token);
//        Workplan plan=workPlanRepository.findById(id).orElse(null);
//        if(plan!=null) {
//            if (userName.toString().equalsIgnoreCase(plan.getAppraiser_email().toString()) &&
//                    plan.getWorkplanStatus().equalsIgnoreCase("PendingApproval")) {
//                Query query = new Query();
//                query.addCriteria(Criteria.where("_id").is(plan.getId()));
//                Update updateworkplan = new Update();
//                updateworkplan.set("user_email", plan.getUser_email());
//                updateworkplan.set("evaluator_email", plan.getEvaluator_email());
//                updateworkplan.set("appraiser_email", SecurityUtils.getCurrentUserLogin().get().toString());
//                updateworkplan.set("evaluationPeriod", plan.getEvaluationPeriod());
//
//                updateworkplan.set("AreasOfPerformance", plan.getAreasOfPerformance());
//                updateworkplan.set("workplanStatus", "Approved");
//                updateworkplan.set("statusComments", plan.getStatusComments());
//                mongoTemplate.findAndModify(query, updateworkplan, new FindAndModifyOptions().returnNew(true), Workplan.class);
//
//                emailService.sendSimpleMessage(plan.getUser_email()+domain, "Workplan Approved ", "Good day your workplan has been approved :"+ "\t" +
//                        " by"+"\n"+ SecurityUtils.getCurrentUserLogin().toString()+"\n"+"at:"+ LocalDateTime.now());
//                createScorecard(plan);
//                Workplan wp1 = workPlanRepository.findById(plan.getId()).orElse(null);
//                if (wp1 != null) {
//                    UserEntity appraiser = userEntityRepository.findById(plan.getUser_email()).orElse(null);
//                    List<String> appraisees = appraiser.getAppraisees();
//                    if (wp1.getWorkplanStatus().equalsIgnoreCase("Approved") | wp1.getWorkplanStatus().equalsIgnoreCase("Ammended")) {
//                        if(appraisees!=null){  if (appraisees.size() == 0 |appraisees==null) {
//                            return "Successfully approved";
//                        }
//                        else {
//                            for (String user : appraisees) {
//                                Workplan wp = new Workplan();
//                                if (wp1.getWorkplanStatus().equalsIgnoreCase("Ammended")) {
//                                    wp.setWorkplanStatus("PendingAmmendApproval");
//                                }
//                                wp.setWorkplanStatus("Incomplete");
//                                wp.setAppraiser_email(wp1.getUser_email());
//                                wp.setEvaluationPeriod(wp1.getEvaluationPeriod());
//                                wp.setUser_email(user);
//
//
//                                List<WorkplanPerformanceArea> areas = new ArrayList<>();
//
//
//
//                                for (WorkplanPerformanceArea wpa : wp1.getAreasOfPerformance()) {
//                                    WorkplanPerformanceArea area = new WorkplanPerformanceArea();
//
//                                    ArrayList<KPI> newprogs = new ArrayList<>();
//
//
//                                    ArrayList<KPI> programs = wpa.getPrograms();
//                                    for (KPI prog : programs) {
//                                        KPI kpi = new KPI();
//
//
//
//                                        ArrayList<MPI> newIndicators = new ArrayList<>();
//
//                                        List<MPI> indicators = prog.getIndicators();
//                                        for (int i = 0; i < indicators.size(); i++) {
//                                            for (ResponsiblePerson person : indicators.get(i).getResponsibleResources()) {
//                                                if (person.getUsername().toString().equalsIgnoreCase(user)) {
//                                                    newIndicators.add(indicators.get(i));
//
//
//                                                }
//                                            }
//
//
////                                    prog.setIndicators(indicators);
//
//
//                                        }
//
//                                        kpi.setName(prog.getName());
//                                        kpi.setWeight(prog.getWeight());
//                                        kpi.setContributedPillar(prog.getContributedPillar());
//                                        kpi.setIndicators(newIndicators);
//                                        newprogs.add(kpi);
//                                    }
//                                    area.setPerformanceArea(wpa.getPerformanceArea());
//                                    area.setWeight(wpa.getWeight());
//                                    area.setSection(wpa.getSection());
//                                    area.setDescription(wpa.getDescription());
//                                    area.setPrograms(newprogs);
//                                    areas.add(area);
//                                }
//                                wp.setAreasOfPerformance(areas);
//                                List<WorkplanPerformanceArea> wp_areas = wp.getAreasOfPerformance();
//                                for (int w = 0; w < wp_areas.size(); w++) {
//                                    List<KPI> wp_programs = wp_areas.get(w).getPrograms();
//                                    for (int j = 0; j < wp_programs.size(); j++) {
//                                        if (wp_programs.get(j).getIndicators().size() < 1) {
//                                            wp.getAreasOfPerformance().remove(wp_areas.get(w));
//                                        }
//                                    }
//                                }
//                                if (wp.getAreasOfPerformance().size() > 0) {
//                                    save(wp);
//                                    emailService.sendSimpleMessage(wp.getUser_email()+domain, "New Workplan ", "Good day you have been assigned  new workplan :"+ "\t" +
//                                            " by"+"\n"+ wp.getAppraiser_email()+"\n"+"at:"+ LocalDateTime.now());
//                                }
//                            }
//                        }}
//
//
//                    }
//
//                    return ("Workplan for \t" + plan.getUser_email() + "\n Successfully approved");
//                }
//            }
//        }
//        return "Can not find workplan";
//    }

    public Long createScorecard(Workplan workplan){
        System.out.println("=== CREATE SCORECARD DEBUG ===");
        System.out.println("Creating scorecard for workplan ID: " + workplan.getId());
        System.out.println("User Email: " + workplan.getUser_email());
        System.out.println("Evaluation Period: " + workplan.getEvaluationPeriod());
        System.out.println("Areas of Performance count: " + (workplan.getAreasOfPerformance() != null ? workplan.getAreasOfPerformance().size() : 0));
        
        Scorecard scorecard=new Scorecard();
        scorecard.setAppraiser(workplan.getAppraiser_email());
        scorecard.setEvaluator(workplan.getEvaluator_email());
        scorecard.setScorecardStatus("WorkingScorecard");
        scorecard.setEvaluationPeriod(workplan.getEvaluationPeriod());
        scorecard.setUser_email(workplan.getUser_email());
        List<ScorecardPerformanceArea> areas = new ArrayList<>();
        for (WorkplanPerformanceArea spa : workplan.getAreasOfPerformance()) {
            ScorecardPerformanceArea area = new ScorecardPerformanceArea();

            ArrayList<ScorecardKPI> newprogs = new ArrayList<>();


            ArrayList<KPI> programs = spa.getPrograms();
            for (KPI prog : programs) {
                ScorecardKPI scorecardkpi = new ScorecardKPI();



                ArrayList<ScorecardMPI> newIndicators = new ArrayList<>();

                List<MPI> indicators = prog.getIndicators();
                for (MPI indicator:indicators) {
                  ScorecardMPI scmpi=new ScorecardMPI();

                    scmpi.setDescription(indicator.getDescription());
                    scmpi.setWeight(indicator.getWeight());
                    scmpi.setAllowable_variance(indicator.getAllowable_variance());
                    scmpi.setIncremental_or_decremental(indicator.getIncremental_or_decremental());
                    scmpi.setAnnual_target(indicator.getAnnual_target());
                    scmpi.setMeasurement_unit(indicator.getMeasurement_unit());
                    scmpi.setQuarterly_target(indicator.getQuarterly_target());
                    scmpi.setResponsibleDivision(indicator.getResponsibleDivision());
                    scmpi.setResponsibleSection(indicator.getResponsibleSection());
                    scmpi.setResponsibleResources(indicator.getResponsibleResources());
                    scmpi.setPrevious_year_Perfomenace(indicator.getPrevious_year_Perfomenace());
                    newIndicators.add(scmpi);





                }

                scorecardkpi.setName(prog.getName());
                scorecardkpi.setWeight(prog.getWeight());
                scorecardkpi.setContributedPillar(prog.getContributedPillar());
                scorecardkpi.setIndicators(newIndicators);
                newprogs.add(scorecardkpi);




            }
            area.setPerformanceArea(spa.getPerformanceArea());
            area.setWeight(spa.getWeight());
            area.setSection(spa.getSection());
            area.setDescription(spa.getDescription());
            area.setPrograms(newprogs);
            areas.add(area);


        }
        scorecard.setAreasOfPerformance(areas);
        System.out.println("Scorecard being saved with " + areas.size() + " performance areas");
        Long scorecardId = scorecardService.saveScorecard(scorecard);
        System.out.println("Scorecard saved with ID: " + scorecardId);
        System.out.println("=== END CREATE SCORECARD DEBUG ===");
        return scorecardId;
    }


    @Override
    public String disApproveWorkplan(Long id, String message) {
        Workplan plan=workPlanRepository.findById(id).orElse(null);
        if(plan!=null) {
            if (SecurityUtils.getCurrentUserLogin().get().toString().equalsIgnoreCase(plan.getAppraiser_email()) &&
                    plan.getWorkplanStatus().equalsIgnoreCase("PendingApproval")) {
                Query query = new Query();
                query.addCriteria(Criteria.where("_id").is(plan.getId()));
                Update updateworkplan = new Update();
                updateworkplan.set("user_email", plan.getUser_email());
                updateworkplan.set("evaluator_email", plan.getEvaluator_email());
                updateworkplan.set("appraiser_email", SecurityUtils.getCurrentUserLogin().get().toString());
                updateworkplan.set("evaluationPeriod", plan.getEvaluationPeriod());

                updateworkplan.set("AreasOfPerformance", plan.getAreasOfPerformance());
                updateworkplan.set("workplanStatus", "Rejected");
                updateworkplan.set("statusComments", message);
                mongoTemplate.findAndModify(query, updateworkplan, new FindAndModifyOptions().returnNew(true), Workplan.class);
                emailService.sendSimpleMessage(plan.getUser_email()+domain, "Workplan Disapproved/Rejected ", "Good day you have been assigned  new workplan :"+ "\t" +
                        " by"+"\n"+ SecurityUtils.getCurrentUserLogin().toString()+"\n"+"at:"+ LocalDateTime.now());
               return ("Workplan for \t" + plan.getUser_email() + "\n has been Rejected or disapproved");
            }
        }
        return "could not find the workplan";
    }


    @Override
    public Workplan deleteIndicator(Long id, String email, String area, String program, String indicator) {

        Workplan workplan = workPlanRepository.findById(id).orElse(null);
        if (workplan != null) {
            List<WorkplanPerformanceArea> areas = workplan.getAreasOfPerformance();
            for (WorkplanPerformanceArea wpa : areas) {
                if (area.equalsIgnoreCase(wpa.getDescription().toString())) {
                    ArrayList<KPI> programs = wpa.getPrograms();
                    for (KPI prog : programs) {
                        if (prog.getName().toString().equalsIgnoreCase(program)) {
                            List<MPI> indicators = prog.getIndicators();
                            for (int i = 0; i < indicators.size(); i++) {

                                if (indicators.get(i).getDescription().toString().equalsIgnoreCase(indicator)) {
                                    indicators.remove(i);


                                }
                            }
                            prog.setIndicators(indicators);
                        }

                    }
                    wpa.setPrograms(programs);

                }
            }
            workplan.setAreasOfPerformance(areas);
            update(workplan);
            return workplan;

        }


//        Query query=new Query();
//        List<Criteria> criteria= new ArrayList<>();
//        if(id!=null ){
//            criteria.add(Criteria.where("_id").is(id));
//        }
//        if(email!=null && !email.isEmpty()){
//            criteria.add(Criteria.where("user_email").is(email));
//        }
//        if(email!=null && !email.isEmpty()){
//            criteria.add(Criteria.where("user_email").is(email));
//        }
//
//
//
//        if(!criteria.isEmpty()) {
//            query.addCriteria(new Criteria().andOperator(criteria.toArray(new Criteria[0])));
//        }
//        Workplan workplan = mongoTemplate.findAndModify(query,update,Workplan.class);
//        return workplan;

        return new Workplan();
    }


    public Workplan exportWorkplanPDF(Principal principal) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(principal.getName()));
        if (mongoTemplate.exists(query, "users_tbl")) {
            UserEntity user = mongoTemplate.findOne(query, UserEntity.class);
            Query query1 = new Query();
            query1.addCriteria(Criteria.where("employee.ecNumber").is(user.getEc_number()));
            return mongoTemplate.findOne(query1, Workplan.class);
        } else
            return null;
    }



    @Override
    public Workplan update(Workplan workplan) {
        boolean valid=false;

        if (workplan.getWorkplanStatus().toString().equalsIgnoreCase("pendingApproval")) {
//            valid= validateWorkplan(workplan);
//            if(valid==true){
                Query query = new Query();
                query.addCriteria(Criteria.where("_id").is(workplan.getId()));
                Update updateworkplan = new Update();
                updateworkplan.set("user_email", workplan.getUser_email());
                updateworkplan.set("evaluator_email", workplan.getEvaluator_email());
                updateworkplan.set("appraiser_email", workplan.getAppraiser_email());
                updateworkplan.set("evaluationPeriod", workplan.getEvaluationPeriod());

                updateworkplan.set("AreasOfPerformance", workplan.getAreasOfPerformance());
                updateworkplan.set("workplanStatus", workplan.getWorkplanStatus());
                updateworkplan.set("statusComments", workplan.getStatusComments());
                updateworkplan.set("dateSubmitted", LocalDateTime.now());
                // Clear dateApproved when re-submitting after rejection
                updateworkplan.set("dateApproved", null);
                return mongoTemplate.findAndModify(query, updateworkplan, new FindAndModifyOptions().returnNew(true), Workplan.class);

//            }
             }

        if (workplan.getWorkplanStatus().toString().equalsIgnoreCase("Incomplete")) {

            Query query = new Query();
            query.addCriteria(Criteria.where("_id").is(workplan.getId()));
            Update updateworkplan = new Update();
            updateworkplan.set("user_email", workplan.getUser_email());
            updateworkplan.set("evaluator_email", workplan.getEvaluator_email());
            updateworkplan.set("appraiser_email", workplan.getAppraiser_email());
            updateworkplan.set("evaluationPeriod", workplan.getEvaluationPeriod());

            updateworkplan.set("AreasOfPerformance", workplan.getAreasOfPerformance());
            updateworkplan.set("workplanStatus", workplan.getWorkplanStatus());
            updateworkplan.set("statusComments", workplan.getStatusComments());
//        UserEntity appraiser = userEntityRepository.findById(workplan.getUser_email()).orElse(null);
//        List<String> appraisees = appraiser.getAppraisees();
//        if (workplan.getWorkplanStatus().equalsIgnoreCase("Approved")|workplan.getWorkplanStatus().equalsIgnoreCase("Ammended")) {
//            if (appraisees.size() == 0) {
//                return mongoTemplate.findAndModify(query, updateworkplan, new FindAndModifyOptions().returnNew(true), Workplan.class);
//            } else {
//                for (String user : appraisees) {
//                    Workplan wp = new Workplan();
//                    if(workplan.getWorkplanStatus().equalsIgnoreCase("Ammended")){
//                        wp.setWorkplanStatus("PendingAmmendApproval");
//                    }
//                    wp.setWorkplanStatus("Incomplete");
//                    wp.setAppraiser_email(workplan.getUser_email());
//                    wp.setEvaluationPeriod(workplan.getEvaluationPeriod());
//                    wp.setUser_email(user);
//
//
//                    List<WorkplanPerformanceArea> areas = new ArrayList<>();
//                    Boolean tracker = false;
//                    Boolean program_tracker = false;
//
//
//
//                    for (WorkplanPerformanceArea wpa : workplan.getAreasOfPerformnce()) {
//                        WorkplanPerformanceArea area = new WorkplanPerformanceArea();
//
//                        ArrayList<KPI> newprogs = new ArrayList<>();
//
////                        if (areas.size() > 0) {
////                            for (WorkplanPerformanceArea a : areas) {
////                                if (a.getDescription()==(wpa.getDescription()) |
////                                        a.getPerformanceArea()==(wpa.getPerformanceArea())) {
////                                    tracker = true;
////                                }
////                            }
////
////
////                        }
//
////                        area.setPrograms(newprogs);
//                        ArrayList<KPI> programs = wpa.getPrograms();
//                        for (KPI prog : programs) {
//                            KPI kpi = new KPI();
//
//
////                                for (KPI k : programs) {
////                                    if (k.getName().toString().equalsIgnoreCase(prog.getName().toString())) {
////                                        program_tracker = true;
////                                    }
////                                }
////                                kpi.setIndicators(newIndicators);
//                                ArrayList<MPI> newIndicators = new ArrayList<>();
//
//                                List<MPI> indicators = prog.getIndicators();
//                                for (int i = 0; i < indicators.size(); i++) {
//                                    for(ResponsiblePerson person: indicators.get(i).getResponsibleResources()){
//                                        if (person.getUsername().toString().equalsIgnoreCase(user)) {
//                                            newIndicators.add(indicators.get(i));
//
//
//
//                                        }
//                                    }
//
//
//
////                                    prog.setIndicators(indicators);
//
//
//                                }
////                                if (program_tracker == false) {
////                                     kpi = new KPI();
////
////                                    kpi.setName(prog.getName());
////                                    kpi.setWeight(prog.getWeight());
////                                    kpi.setContributedPillar(prog.getContributedPillar());
////                                    kpi.setIndicators(newIndicators);
////                                    newprogs.add(kpi);
////
////                                }
//                                kpi.setName(prog.getName());
//                                kpi.setWeight(prog.getWeight());
//                                kpi.setContributedPillar(prog.getContributedPillar());
//                                kpi.setIndicators(newIndicators);
//                                newprogs.add(kpi);
//
//
//
////                        area.setPrograms(newprogs);
////                        areas.add(area);
////                            if (tracker == false) {
////                                area = new WorkplanPerformanceArea();
////                                area.setPerformanceArea(wpa.getPerformanceArea());
////                                area.setWeight(wpa.getWeight());
////                                area.setSection(wpa.getSection());
////                                area.setDescription(wpa.getDescription());
////                                area.setPrograms(newprogs);
////                                areas.add(area);
////                            }
//
//                        }
//                        area.setPerformanceArea(wpa.getPerformanceArea());
//                        area.setWeight(wpa.getWeight());
//                        area.setSection(wpa.getSection());
//                        area.setDescription(wpa.getDescription());
//                        area.setPrograms(newprogs);
//                        areas.add(area);
//
//
//
//
//                    }
//                    wp.setAreasOfPerformnce(areas);
//                    List<WorkplanPerformanceArea> wp_areas=wp.getAreasOfPerformnce();
//                    for(int w=0;w<wp_areas.size();w++){
//                        List<KPI> wp_programs=wp_areas.get(w).getPrograms();
//                        for(int j=0;j<wp_programs.size();j++){
//                            if(wp_programs.get(j).getIndicators().size()<1){
//                                wp.getAreasOfPerformnce().remove(wp_areas.get(w));
//                            }
//                        }
//
//                    }
//                    if(wp.getAreasOfPerformnce().size()>0){
//                    save(wp);}
//                }
//            }
//
//
//        }
            return mongoTemplate.findAndModify(query, updateworkplan, new FindAndModifyOptions().returnNew(true), Workplan.class);
        }
       if (workplan.getWorkplanStatus().toString().equalsIgnoreCase("Rejected")) {

            Query query = new Query();
            query.addCriteria(Criteria.where("_id").is(workplan.getId()));
            Update updateworkplan = new Update();
            updateworkplan.set("user_email", workplan.getUser_email());
            updateworkplan.set("evaluator_email", workplan.getEvaluator_email());
            updateworkplan.set("appraiser_email", workplan.getAppraiser_email());
            updateworkplan.set("evaluationPeriod", workplan.getEvaluationPeriod());

            updateworkplan.set("AreasOfPerformance", workplan.getAreasOfPerformance());
            updateworkplan.set("workplanStatus", workplan.getWorkplanStatus());
            updateworkplan.set("statusComments", workplan.getStatusComments());

            return mongoTemplate.findAndModify(query, updateworkplan, new FindAndModifyOptions().returnNew(true), Workplan.class);
        }
        return new Workplan();
    }

    public boolean validateWorkplan(Workplan plan){
        ArrayList<Integer> indicatorsWeight = new ArrayList<>();
        ArrayList<Integer> programsWeight = new ArrayList<>();
        ArrayList<Integer> areasWeight = new ArrayList<>();
        int initial_weight=0;
        for (WorkplanPerformanceArea wpa : plan.getAreasOfPerformance()) {
            areasWeight.add(wpa.getWeight());



//            ArrayList<Integer> programsWeight = new ArrayList<>();


            ArrayList<KPI> programs = wpa.getPrograms();
            int initial_program=0;

            for (KPI prog : programs) {
//                programsWeight.add(prog.getWeight());



//                ArrayList<Integer> indicatorsWeight = new ArrayList<>();
                int initial_indicator=0;
                List<MPI> indicators = prog.getIndicators();
                for (int i = 0; i < indicators.size(); i++) {
                    indicatorsWeight.add(indicators.get(i).getWeight());
                    initial_indicator=initial_indicator+indicators.get(i).getWeight();






                }
                if(prog.getWeight()==initial_indicator){
                    programsWeight.add(prog.getWeight());
                    initial_program=initial_program+prog.getWeight();
                }
                else {
                    return false;
                }


            }
            if(initial_program==wpa.getWeight()){
                areasWeight.add(wpa.getWeight());
                initial_weight=initial_weight+wpa.getWeight();
            }
            else{
                return false;
            }
        }
        if(initial_weight<100|initial_weight>100){
            return false;

        }
        return true;
    }

    // Board-specific methods for grade 0 users to manage grade 1 workplans
    @Override
    public List<Workplan> searchWorkplanForBoard(String period, String planStatus) {
        Query query = new Query();
        List<Criteria> criteria = new ArrayList<>();
        
        // Filter by evaluation period if provided
        if (period != null && !period.isEmpty()) {
            criteria.add(Criteria.where("evaluationPeriod").is(period));
        }
        
        // Filter by workplan status if provided (skip for "ALL")
        if (planStatus != null && !planStatus.isEmpty() && !"ALL".equalsIgnoreCase(planStatus)) {
            criteria.add(Criteria.where("workplanStatus").is(planStatus));
        } else if (planStatus == null || planStatus.isEmpty()) {
            // Default to pending if nothing specified
            criteria.add(Criteria.where("workplanStatus").is("pendingApproval"));
        }
        
        if (!criteria.isEmpty()) {
            query.addCriteria(new Criteria().andOperator(criteria.toArray(new Criteria[0])));
        }
        
        // Get all workplans matching the criteria
        List<Workplan> allWorkplans = mongoTemplate.find(query, Workplan.class);
        
        // Filter to only include workplans from grade 1 users
        List<Workplan> grade1Workplans = new ArrayList<>();
        for (Workplan workplan : allWorkplans) {
            UserEntity user = userEntityRepository.findById(workplan.getUser_email()).orElse(null);
            if (user != null && "1".equals(user.getGrade())) {
                grade1Workplans.add(workplan);
            }
        }
        
        return grade1Workplans;
    }

    @Override
    public String approveBoardWorkplan(Long id, String boardMemberEmail) {
        Workplan plan = workPlanRepository.findById(id).orElse(null);
        
        if (plan == null) {
            return "Workplan not found";
        }
        
        // Verify the user is grade 1
        UserEntity user = userEntityRepository.findById(plan.getUser_email()).orElse(null);
        if (user == null || !"1".equals(user.getGrade())) {
            return "This workplan is not from a grade 1 user";
        }
        
        // Verify the board member is grade 0
        UserEntity boardMember = userEntityRepository.findById(boardMemberEmail).orElse(null);
        if (boardMember == null || !"0".equals(boardMember.getGrade())) {
            return "Only Board members (grade 0) can approve this workplan";
        }
        
        // Update workplan status to Approved
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(plan.getId()));
        Update updateworkplan = new Update();
        updateworkplan.set("user_email", plan.getUser_email());
        updateworkplan.set("evaluator_email", plan.getEvaluator_email());
        updateworkplan.set("appraiser_email", plan.getAppraiser_email());
        updateworkplan.set("evaluationPeriod", plan.getEvaluationPeriod());
        updateworkplan.set("AreasOfPerformance", plan.getAreasOfPerformance());
        updateworkplan.set("workplanStatus", "Approved");
        updateworkplan.set("statusComments", "Approved by Board: " + boardMemberEmail);
        updateworkplan.set("dateApproved", LocalDateTime.now());
        
        mongoTemplate.findAndModify(query, updateworkplan, new FindAndModifyOptions().returnNew(true), Workplan.class);
        
        // Send email notification
        try {
            emailService.sendSimpleMessage(
                plan.getUser_email() + domain,
                "Workplan Approved by Board",
                "Good day, your workplan has been approved by the Board.\n" +
                "Approved by: " + boardMemberEmail + "\n" +
                "Date: " + LocalDateTime.now()
            );
        } catch (Exception e) {
            System.out.println("WARNING: Failed to send Board approval email: " + e.getMessage());
        }
        
        // Create scorecard
        createScorecard(plan);
        
        return "Workplan for " + plan.getUser_email() + " successfully approved by Board";
    }

    @Override
    public String rejectBoardWorkplan(Long id, String boardMemberEmail, String rejectionReason) {
        Workplan plan = workPlanRepository.findById(id).orElse(null);
        
        if (plan == null) {
            return "Workplan not found";
        }
        
        // Verify the user is grade 1
        UserEntity user = userEntityRepository.findById(plan.getUser_email()).orElse(null);
        if (user == null || !"1".equals(user.getGrade())) {
            return "This workplan is not from a grade 1 user";
        }
        
        // Verify the board member is grade 0
        UserEntity boardMember = userEntityRepository.findById(boardMemberEmail).orElse(null);
        if (boardMember == null || !"0".equals(boardMember.getGrade())) {
            return "Only Board members (grade 0) can reject this workplan";
        }
        
        // Update workplan status to Rejected
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(plan.getId()));
        Update updateworkplan = new Update();
        updateworkplan.set("user_email", plan.getUser_email());
        updateworkplan.set("evaluator_email", plan.getEvaluator_email());
        updateworkplan.set("appraiser_email", plan.getAppraiser_email());
        updateworkplan.set("evaluationPeriod", plan.getEvaluationPeriod());
        updateworkplan.set("AreasOfPerformance", plan.getAreasOfPerformance());
        updateworkplan.set("workplanStatus", "Rejected");
        updateworkplan.set("statusComments", "Rejected by Board (" + boardMemberEmail + "): " + rejectionReason);
        
        mongoTemplate.findAndModify(query, updateworkplan, new FindAndModifyOptions().returnNew(true), Workplan.class);
        
        // Send email notification
        try {
            emailService.sendSimpleMessage(
                plan.getUser_email() + domain,
                "Workplan Rejected by Board",
                "Your workplan has been rejected by the Board.\n" +
                "Rejected by: " + boardMemberEmail + "\n" +
                "Reason: " + rejectionReason + "\n" +
                "Date: " + LocalDateTime.now() + "\n\n" +
                "Please review the comments and resubmit your workplan."
            );
        } catch (Exception e) {
            System.out.println("WARNING: Failed to send Board rejection email: " + e.getMessage());
        }
        
        return "Workplan for " + plan.getUser_email() + " rejected by Board";
    }
}