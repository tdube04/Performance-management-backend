package com.innovation.workplan.ServiceImplementations;

import com.innovation.workplan.CollectionModels.Scorecard;
import com.innovation.workplan.CollectionModels.UserEntity;
import com.innovation.workplan.CollectionModels.Workplan;
import com.innovation.workplan.Configuration.SecurityUtils;
import com.innovation.workplan.Repositories.ScorecardRepository;
import com.innovation.workplan.Services.EmailService;
import com.innovation.workplan.Services.ScorecardService;
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

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service

public class ScorecardServiceImpl implements ScorecardService {
    @Autowired
    private ScorecardRepository scorecardRepository;
    @Autowired

    private EmailService emailService;

    @Autowired
    SequenceGeneratorService sequenceGenerator;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Override
    public Long saveScorecard(Scorecard scorecard) {
        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(scorecard.getId()));
        if (mongoTemplate.exists(query, "Scorecards_tbl")){
            return 9000l;
        }
        else {
            scorecard.setId(sequenceGenerator.generateSequence(Scorecard.SEQUENCE_NAME));
            return scorecardRepository.save(scorecard).getId();
        }
    }

    @Override
    public Page<Scorecard> searchScorecard(Long id, String username, String period, Pageable pageable) {
        Query query=new Query().with(pageable);
        List<Criteria> criteria= new ArrayList<>();
        if(id!=null ){
            criteria.add(Criteria.where("_id").is(id));
        }
        if(username!=null && !username.isEmpty()){
            criteria.add(Criteria.where("user_email").is(username));
        }


        if(period!=null && !period.isEmpty()){
            criteria.add(Criteria.where("evaluationPeriod").is(period));
        }

        if(!criteria.isEmpty()) {
            query.addCriteria(new Criteria().andOperator(criteria.toArray(new Criteria[0])));
        }
        Page<Scorecard> plans= PageableExecutionUtils.getPage(mongoTemplate.find(query,Scorecard.class),
                pageable,()->mongoTemplate
                        .count(query.skip(0).limit(0),Scorecard
                                .class));
        return plans;
    }

    public Scorecard exportScorecardPDF(Principal principal){
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(principal.getName()));
        if (mongoTemplate.exists(query, "users_tbl")) {
            UserEntity user = mongoTemplate.findOne(query, UserEntity.class);
            Query query1 = new Query();
            query1.addCriteria(Criteria.where("employee.ecNumber").is(user.getEc_number()));
            return mongoTemplate.findOne(query1, Scorecard.class);
        }
        else
            return null;
    }


    @Override
    public List<Scorecard> searchScorecardByEvaluator(String evaluator_email, String period, String scorecradStatus) {
        Query query = new Query();
        List<Criteria> criteria = new ArrayList<>();
        if (evaluator_email != null) {
            criteria.add(Criteria.where("evaluator").is(evaluator_email));
        }
        if (period != null && !period.isEmpty()) {
            criteria.add(Criteria.where("evaluationPeriod").is(period));
        }


        if (scorecradStatus != null && !scorecradStatus.isEmpty()) {
            criteria.add(Criteria.where("scorecardStatus").is(scorecradStatus));
        }
        if (!criteria.isEmpty()) {
            query.addCriteria(new Criteria().andOperator(criteria.toArray(new Criteria[0])));
        }
        List<Scorecard> scorecards = mongoTemplate.find(query, Scorecard.class);
        return scorecards;
    }

    @Override
    public List<Scorecard> searchScorecardByStatus(String scorecardStatus) {
        Query query = new Query();
        List<Criteria> criteria = new ArrayList<>();
        if (scorecardStatus != null && !scorecardStatus.isEmpty()) {
            criteria.add(Criteria.where("scorecardStatus").is(scorecardStatus));
        }
        if (!criteria.isEmpty()) {
            query.addCriteria(new Criteria().andOperator(criteria.toArray(new Criteria[0])));
        }
        List<Scorecard> scorecards = mongoTemplate.find(query, Scorecard.class);
        return scorecards;

    }

    @Override
    public String update(Scorecard scorecard) {
        if (scorecard.getScorecardStatus().toString().equalsIgnoreCase("WorkingScorecard")|scorecard
                .getScorecardStatus().toString().equalsIgnoreCase("ResultsScorecard")) {

            Query query = new Query();
            query.addCriteria(Criteria.where("_id").is(scorecard.getId()));
            Update updatescorecard = new Update();
            updatescorecard.set("user_email", scorecard.getUser_email());
            updatescorecard.set("evaluator", scorecard.getEvaluator());
            updatescorecard.set("appraiser", scorecard.getAppraiser());
            updatescorecard.set("evaluationPeriod", scorecard.getEvaluationPeriod());

            updatescorecard.set("AreasOfPerformance", scorecard.getAreasOfPerformance());
            updatescorecard.set("scorecardStatus", scorecard.getScorecardStatus());
            updatescorecard.set("total_overal_weighted_score", scorecard.getTotal_overal_weighted_score());
            updatescorecard.set("summaryList", scorecard.getSummaryList());
            updatescorecard.set("scorecardStatusComment", scorecard.getScorecardStatusComment());
            mongoTemplate.findAndModify(query, updatescorecard, new FindAndModifyOptions().returnNew(true), Scorecard.class);
            return "successfully updated";
        }
        if (scorecard.getScorecardStatus().toString().equalsIgnoreCase("Approved")) {

            Query query = new Query();
            query.addCriteria(Criteria.where("_id").is(scorecard.getId()));
            Update updatescorecard = new Update();
            updatescorecard.set("user_email", scorecard.getUser_email());
            updatescorecard.set("evaluator", scorecard.getEvaluator());
            updatescorecard.set("appraiser", scorecard.getAppraiser());
            updatescorecard.set("evaluationPeriod", scorecard.getEvaluationPeriod());

            updatescorecard.set("AreasOfPerformance", scorecard.getAreasOfPerformance());
            updatescorecard.set("scorecardStatus", scorecard.getScorecardStatus());
            updatescorecard.set("total_overal_weighted_score", scorecard.getTotal_overal_weighted_score());
            updatescorecard.set("summaryList", scorecard.getSummaryList());
            updatescorecard.set("scorecardStatusComment", scorecard.getScorecardStatusComment());
            mongoTemplate.findAndModify(query, updatescorecard, new FindAndModifyOptions().returnNew(true), Scorecard.class);
            return "successfully updated";
        }
        if (scorecard.getScorecardStatus().toString().equalsIgnoreCase("Rejected")) {

            Query query = new Query();
            query.addCriteria(Criteria.where("_id").is(scorecard.getId()));
            Update updatescorecard = new Update();
            updatescorecard.set("user_email", scorecard.getUser_email());
            updatescorecard.set("evaluator", scorecard.getEvaluator());
            updatescorecard.set("appraiser", scorecard.getAppraiser());
            updatescorecard.set("evaluationPeriod", scorecard.getEvaluationPeriod());

            updatescorecard.set("AreasOfPerformance", scorecard.getAreasOfPerformance());
            updatescorecard.set("scorecardStatus", scorecard.getScorecardStatus());
            updatescorecard.set("total_overal_weighted_score", scorecard.getTotal_overal_weighted_score());
            updatescorecard.set("summaryList", scorecard.getSummaryList());
            updatescorecard.set("scorecardStatusComment", scorecard.getScorecardStatusComment());
            mongoTemplate.findAndModify(query, updatescorecard, new FindAndModifyOptions().returnNew(true), Scorecard.class);
            return "successfully updated";
        }
        return "failed to update";


    }

    @Override
    public Scorecard searchUserScorecard(String User_email, String period, String planStatus) {
        Query query = new Query();
        List<Criteria> criteria = new ArrayList<>();
        if (User_email != null) {
            criteria.add(Criteria.where("user_email").is(User_email));
        }
        if (period != null && !period.isEmpty()) {
            criteria.add(Criteria.where("evaluationPeriod").is(period));
        }


        if (planStatus != null && !planStatus.isEmpty()) {
            criteria.add(Criteria.where("scorecardStatus").is(planStatus));
        }
        if (!criteria.isEmpty()) {
            query.addCriteria(new Criteria().andOperator(criteria.toArray(new Criteria[0])));
        }
        Scorecard scorecard = mongoTemplate.findOne(query, Scorecard.class);
        return scorecard;
    }
}