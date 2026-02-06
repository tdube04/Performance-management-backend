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
        
        System.out.println("=== SCORECARD SEARCH DEBUG ===");
        System.out.println("Searching for - ID: " + id + ", Username: " + username + ", Period: " + period);
        
        if(id!=null ){
            criteria.add(Criteria.where("_id").is(id));
        }
        if(username!=null && !username.isEmpty()){
            criteria.add(Criteria.where("user_email").is(username));
        }


        if(period!=null && !period.isEmpty()){
            criteria.add(Criteria.where("evaluationPeriod").is(period));
        }

        // Removed status filter to allow viewing scorecards at any stage (WorkingScorecard or ResultsScorecard)

        if(!criteria.isEmpty()) {
            query.addCriteria(new Criteria().andOperator(criteria.toArray(new Criteria[0])));
        }
        
        System.out.println("Query: " + query.toString());
        
        Page<Scorecard> plans= PageableExecutionUtils.getPage(mongoTemplate.find(query,Scorecard.class),
                pageable,()->mongoTemplate
                        .count(query.skip(0).limit(0),Scorecard
                                .class));
        
        System.out.println("Total Elements Found: " + plans.getTotalElements());
        System.out.println("=== END DEBUG ===");
        
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
        System.out.println("=== SCORECARD UPDATE DEBUG ===");
        System.out.println("Scorecard ID: " + scorecard.getId());
        System.out.println("Scorecard Status: " + scorecard.getScorecardStatus());
        System.out.println("User Email: " + scorecard.getUser_email());
        
        if (scorecard.getScorecardStatus().toString().equalsIgnoreCase("WorkingScorecard") || scorecard
                .getScorecardStatus().toString().equalsIgnoreCase("ResultsScorecard")) {

            System.out.println("Status matches WorkingScorecard or ResultsScorecard");
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
            
            Scorecard updatedScorecard = mongoTemplate.findAndModify(query, updatescorecard, new FindAndModifyOptions().returnNew(true), Scorecard.class);
            System.out.println("Updated Scorecard Status in DB: " + (updatedScorecard != null ? updatedScorecard.getScorecardStatus() : "NULL"));
            System.out.println("=== END UPDATE DEBUG ===");
            return "successfully updated";
        }
        if (scorecard.getScorecardStatus().toString().equalsIgnoreCase("Approved")) {

            System.out.println("Status matches Approved");
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
            
            Scorecard updatedScorecard = mongoTemplate.findAndModify(query, updatescorecard, new FindAndModifyOptions().returnNew(true), Scorecard.class);
            System.out.println("Updated Scorecard Status to Approved in DB: " + (updatedScorecard != null ? updatedScorecard.getScorecardStatus() : "NULL"));
            return "successfully updated";
        }
        if (scorecard.getScorecardStatus().toString().equalsIgnoreCase("Rejected")) {

            System.out.println("Status matches Rejected");
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
            
            Scorecard updatedScorecard = mongoTemplate.findAndModify(query, updatescorecard, new FindAndModifyOptions().returnNew(true), Scorecard.class);
            System.out.println("Updated Scorecard Status to Rejected in DB: " + (updatedScorecard != null ? updatedScorecard.getScorecardStatus() : "NULL"));
            return "successfully updated";
        }
        System.out.println("Status does not match any condition - returning failed to update");
        System.out.println("=== END UPDATE DEBUG ===");
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

    @Autowired
    private com.innovation.workplan.Repositories.UserEntityRepository userEntityRepository;

    private final String domain = "@zimra.co.zw";

    // Board-specific methods for grade 0 users to manage grade 1 scorecards
    @Override
    public List<Scorecard> searchScorecardForBoard(String period, String scorecardStatus) {
        Query query = new Query();
        List<Criteria> criteria = new ArrayList<>();
        
        // Filter by evaluation period if provided
        if (period != null && !period.isEmpty()) {
            criteria.add(Criteria.where("evaluationPeriod").is(period));
        }
        
        // Filter by scorecard status if provided
        if (scorecardStatus != null && !scorecardStatus.isEmpty() && !"ALL".equalsIgnoreCase(scorecardStatus)) {
             criteria.add(Criteria.where("scorecardStatus").is(scorecardStatus));
        } else if (scorecardStatus == null || scorecardStatus.isEmpty()) {
             // Default to pending if nothing specified
             criteria.add(Criteria.where("scorecardStatus").is("ResultsScorecard"));
        }

        if (!criteria.isEmpty()) {
            query.addCriteria(new Criteria().andOperator(criteria.toArray(new Criteria[0])));
        }
        
        // Get all scorecards matching the criteria
        List<Scorecard> allScorecards = mongoTemplate.find(query, Scorecard.class);
        
        // Filter to only include scorecards from grade 1 users
        List<Scorecard> grade1Scorecards = new ArrayList<>();
        for (Scorecard scorecard : allScorecards) {
            UserEntity user = userEntityRepository.findById(scorecard.getUser_email()).orElse(null);
            if (user != null && "1".equals(user.getGrade())) {
                grade1Scorecards.add(scorecard);
            }
        }
        
        return grade1Scorecards;
    }

    @Override
    public String approveBoardScorecard(Long id, String boardMemberEmail) {
        Scorecard scorecard = scorecardRepository.findById(id).orElse(null);
        
        if (scorecard == null) {
            return "Scorecard not found";
        }
        
        // Verify the user is grade 1
        UserEntity user = userEntityRepository.findById(scorecard.getUser_email()).orElse(null);
        if (user == null || !"1".equals(user.getGrade())) {
            return "This scorecard is not from a grade 1 user";
        }
        
        // Verify the board member is grade 0
        UserEntity boardMember = userEntityRepository.findById(boardMemberEmail).orElse(null);
        if (boardMember == null) {
            return "Board member not found with ID: '" + boardMemberEmail + "'";
        }
        if (!"0".equals(boardMember.getGrade())) {
            return "User '" + boardMemberEmail + "' is not a Board member. Grade is: '" + boardMember.getGrade() + "'";
        }
        
        // Update scorecard status to Approved
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(scorecard.getId()));
        Update updateScorecard = new Update();
        updateScorecard.set("user_email", scorecard.getUser_email());
        updateScorecard.set("evaluator", scorecard.getEvaluator());
        updateScorecard.set("appraiser", scorecard.getAppraiser());
        updateScorecard.set("evaluationPeriod", scorecard.getEvaluationPeriod());
        updateScorecard.set("AreasOfPerformance", scorecard.getAreasOfPerformance());
        updateScorecard.set("scorecardStatus", "Approved");
        updateScorecard.set("total_overal_weighted_score", scorecard.getTotal_overal_weighted_score());
        updateScorecard.set("summaryList", scorecard.getSummaryList());
        updateScorecard.set("scorecardStatusComment", "Approved by Board: " + boardMemberEmail);
        
        mongoTemplate.findAndModify(query, updateScorecard, new FindAndModifyOptions().returnNew(true), Scorecard.class);
        
        // Send email notification
        /*try {
            emailService.sendSimpleMessage(
                scorecard.getUser_email() + domain,
                "Scorecard Approved by Board",
                "Good day, your scorecard has been approved by the Board.\n" +
                "Approved by: " + boardMemberEmail + "\n" +
                "Date: " + LocalDateTime.now()
            );
        } catch (Exception e) {
            System.out.println("WARNING: Failed to send Board scorecard approval email: " + e.getMessage());
        }*/
        
        return "Scorecard for " + scorecard.getUser_email() + " successfully approved by Board";
    }

    @Override
    public String rejectBoardScorecard(Long id, String boardMemberEmail, String rejectionReason) {
        Scorecard scorecard = scorecardRepository.findById(id).orElse(null);
        
        if (scorecard == null) {
            return "Scorecard not found";
        }
        
        // Verify the user is grade 1
        UserEntity user = userEntityRepository.findById(scorecard.getUser_email()).orElse(null);
        if (user == null || !"1".equals(user.getGrade())) {
            return "This scorecard is not from a grade 1 user";
        }
        
        // Verify the board member is grade 0
        UserEntity boardMember = userEntityRepository.findById(boardMemberEmail).orElse(null);
        if (boardMember == null || !"0".equals(boardMember.getGrade())) {
            return "Only Board members (grade 0) can reject this scorecard";
        }
        
        // Update scorecard status to Rejected
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(scorecard.getId()));
        Update updateScorecard = new Update();
        updateScorecard.set("user_email", scorecard.getUser_email());
        updateScorecard.set("evaluator", scorecard.getEvaluator());
        updateScorecard.set("appraiser", scorecard.getAppraiser());
        updateScorecard.set("evaluationPeriod", scorecard.getEvaluationPeriod());
        updateScorecard.set("AreasOfPerformance", scorecard.getAreasOfPerformance());
        updateScorecard.set("scorecardStatus", "Rejected");
        updateScorecard.set("total_overal_weighted_score", scorecard.getTotal_overal_weighted_score());
        updateScorecard.set("summaryList", scorecard.getSummaryList());
        updateScorecard.set("scorecardStatusComment", "Rejected by Board (" + boardMemberEmail + "): " + rejectionReason);
        
        mongoTemplate.findAndModify(query, updateScorecard, new FindAndModifyOptions().returnNew(true), Scorecard.class);
        
        // Send email notification
        /*try {
            emailService.sendSimpleMessage(
                scorecard.getUser_email() + domain,
                "Scorecard Rejected by Board",
                "Your scorecard has been rejected by the Board.\n" +
                "Rejected by: " + boardMemberEmail + "\n" +
                "Reason: " + rejectionReason + "\n" +
                "Date: " + LocalDateTime.now() + "\n\n" +
                "Please review the comments and resubmit your scorecard."
            );
        } catch (Exception e) {
            System.out.println("WARNING: Failed to send Board scorecard rejection email: " + e.getMessage());
        }*/
        
        return "Scorecard for " + scorecard.getUser_email() + " rejected by Board";
    }
}