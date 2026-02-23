package com.innovation.workplan.ServiceImplementations;

import com.innovation.workplan.CollectionModels.Scorecard;
import com.innovation.workplan.CollectionModels.ScorecardPerformanceArea;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
    
    // Helper method to filter out empty performance areas and programs
    private Scorecard filterEmptyPerformanceAreas(Scorecard scorecard) {
        if (scorecard.getAreasOfPerformance() == null) {
            return scorecard;
        }
        
        List<ScorecardPerformanceArea> filteredAreas = new ArrayList<>();
        
        for (ScorecardPerformanceArea area : scorecard.getAreasOfPerformance()) {
            // Skip empty performance areas
            if (area.getPerformanceArea() == null || area.getPerformanceArea().trim().isEmpty()) {
                continue;
            }
            
            // Filter out programs with empty names
            if (area.getPrograms() != null) {
                List<com.innovation.workplan.CollectionModels.KPI> filteredPrograms = new ArrayList<>();
                for (com.innovation.workplan.CollectionModels.KPI program : area.getPrograms()) {
                    if (program.getName() != null && !program.getName().trim().isEmpty()) {
                        filteredPrograms.add(program);
                    }
                }
                area.setPrograms(filteredPrograms);
            }
            
            // Only add areas that have valid programs
            if (area.getPrograms() != null && !area.getPrograms().isEmpty()) {
                filteredAreas.add(area);
            }
        }
        
        scorecard.setAreasOfPerformance(filteredAreas);
        return scorecard;
    }
    
    @Override
    public Long saveScorecard(Scorecard scorecard) {
        // Filter out empty performance areas before saving
        scorecard = filterEmptyPerformanceAreas(scorecard);
        
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
        
        // Filter out empty performance areas before updating
        scorecard = filterEmptyPerformanceAreas(scorecard);
        
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
            updatescorecard.set("dateSubmitted", LocalDateTime.now());
            // Clear dateApproved when re-submitting after rejection
            updatescorecard.set("dateApproved", null);
            
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
            updatescorecard.set("dateApproved", LocalDateTime.now());
            
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
        updateScorecard.set("dateApproved", LocalDateTime.now());
        
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

    // Appraisee Confirmation Method
    @Override
    public String confirmScorecard(Long id, Scorecard scorecard) {
        Scorecard existingScorecard = scorecardRepository.findById(id).orElse(null);
        
        if (existingScorecard == null) {
            return "Scorecard not found";
        }
        
        // Only allow confirmation if scorecard is Approved
        if (!"Approved".equals(existingScorecard.getScorecardStatus())) {
            return "Scorecard must be Approved before confirmation";
        }
        
        // Update confirmation fields
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(id));
        Update updateScorecard = new Update();
        
        updateScorecard.set("appraiseeConfirmed", scorecard.getAppraiseeConfirmed());
        updateScorecard.set("appraiseeConfirmedAt", LocalDateTime.now());
        updateScorecard.set("confirmationStatus", scorecard.getConfirmationStatus());
        updateScorecard.set("appraiseeComments", scorecard.getAppraiseeComments());
        updateScorecard.set("forwardedToHC", scorecard.getForwardedToHC());
        updateScorecard.set("forwardedToHCAt", scorecard.getForwardedToHC() ? LocalDateTime.now() : null);
        updateScorecard.set("hcStatus", "PENDING_HC");
        
        mongoTemplate.findAndModify(query, updateScorecard, new FindAndModifyOptions().returnNew(true), Scorecard.class);
        
        return "Scorecard confirmed successfully and forwarded to Human Capital";
    }

    // HC Dashboard Methods
    @Override
    public List<Scorecard> searchAllScorecards(String period, String scorecardStatus, String grade, String division, String section) {
        Query query = new Query();
        List<Criteria> criteria = new ArrayList<>();
        
        if (period != null && !period.isEmpty()) {
            criteria.add(Criteria.where("evaluationPeriod").is(period));
        }
        
        if (scorecardStatus != null && !scorecardStatus.isEmpty() && !"ALL".equalsIgnoreCase(scorecardStatus)) {
            criteria.add(Criteria.where("scorecardStatus").is(scorecardStatus));
        }
        
        if (!criteria.isEmpty()) {
            query.addCriteria(new Criteria().andOperator(criteria.toArray(new Criteria[0])));
        }
        
        List<Scorecard> allScorecards = mongoTemplate.find(query, Scorecard.class);
        
        // Filter by user properties if provided
        List<Scorecard> filteredScorecards = new ArrayList<>();
        for (Scorecard scorecard : allScorecards) {
            UserEntity user = userEntityRepository.findById(scorecard.getUser_email()).orElse(null);
            if (user != null) {
                boolean matches = true;
                
                if (grade != null && !grade.isEmpty() && !grade.equals(user.getGrade())) {
                    matches = false;
                }
                if (division != null && !division.isEmpty() && !division.equals(user.getDivisionName())) {
                    matches = false;
                }
                if (section != null && !section.isEmpty() && !section.equals(user.getSectionName())) {
                    matches = false;
                }
                
                if (matches) {
                    filteredScorecards.add(scorecard);
                }
            }
        }
        
        return filteredScorecards;
    }

    @Override
    public java.util.Map<String, Object> getHCSummary(String period) {
        java.util.Map<String, Object> summary = new java.util.HashMap<>();
        
        Query query = new Query();
        if (period != null && !period.isEmpty()) {
            query.addCriteria(Criteria.where("evaluationPeriod").is(period));
        }
        
        List<Scorecard> allScorecards = mongoTemplate.find(query, Scorecard.class);
        
        int totalUsers = 0;
        int submitted = 0;
        int pending = 0;
        int postQuarterEnd = 0;
        
        for (Scorecard scorecard : allScorecards) {
            if (scorecard.getForwardedToHC() != null && scorecard.getForwardedToHC()) {
                submitted++;
                if (scorecard.getSubmittedAfterQuarterEnd() != null && scorecard.getSubmittedAfterQuarterEnd()) {
                    postQuarterEnd++;
                }
            } else {
                pending++;
            }
        }
        
        totalUsers = allScorecards.size();
        
        summary.put("totalUsers", totalUsers);
        summary.put("submitted", submitted);
        summary.put("pending", pending);
        summary.put("postQuarterEnd", postQuarterEnd);
        
        return summary;
    }

    // HC Receive Scorecard Method
    @Override
    public String markScorecardReceivedByHC(Long id, String hcEmail) {
        Scorecard scorecard = scorecardRepository.findById(id).orElse(null);
        
        if (scorecard == null) {
            return "Scorecard not found";
        }
        
        // Verify the scorecard was forwarded to HC
        if (scorecard.getForwardedToHC() == null || !scorecard.getForwardedToHC()) {
            return "This scorecard has not been forwarded to HC";
        }
        
        // Verify the HC user exists and has HC access
        UserEntity hcUser = userEntityRepository.findById(hcEmail).orElse(null);
        if (hcUser == null) {
            return "HC user not found";
        }
        
        // Update hcStatus to UNDER_REVIEW_HC and set received date
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(id));
        Update updateScorecard = new Update();
        updateScorecard.set("hcStatus", "UNDER_REVIEW_HC");
        updateScorecard.set("hcReceivedAt", LocalDateTime.now());
        updateScorecard.set("hcReceivedBy", hcEmail);
        
        mongoTemplate.findAndModify(query, updateScorecard, new FindAndModifyOptions().returnNew(true), Scorecard.class);
        
        return "Scorecard marked as received by HC";
    }

    // HC Dashboard comprehensive stats from scorecard data
    @Override
    public Map<String, Object> getHCDashboardStats(String period) {
        Map<String, Object> stats = new HashMap<>();
        
        Query query = new Query();
        // Filter by period if provided
        if (period != null && !period.isEmpty()) {
            query.addCriteria(Criteria.where("evaluationPeriod").is(period));
        }
        
        List<Scorecard> allScorecards = mongoTemplate.find(query, Scorecard.class);
        
        // Get all users for joining
        List<UserEntity> allUsers = userEntityRepository.findAll();
        Map<String, UserEntity> userMap = new HashMap<>();
        for (UserEntity user : allUsers) {
            userMap.put(user.getUsername(), user);
        }
        
        // Calculate stats from scorecards
        int totalUsers = allUsers.size();
        int submitted = 0;
        int pending = 0;
        int postQuarterEnd = 0;
        int forwardedToHC = 0;
        
        // For division and grade stats
        Map<String, Map<String, Integer>> divisionStats = new HashMap<>();
        Map<String, Map<String, Integer>> gradeStats = new HashMap<>();
        
        // For score distribution
        int score6 = 0; // Clearly exceeds
        int score5 = 0; // Above targets
        int score4 = 0; // Met targets
        int score3 = 0; // Below targets within variance
        int score2 = 0; // Below targets below variance
        int score1 = 0; // Nothing accomplished
        
        // Top and bottom performers
        List<Map<String, Object>> topPerformers = new ArrayList<>();
        List<Map<String, Object>> bottomPerformers = new ArrayList<>();
        
        for (Scorecard scorecard : allScorecards) {
            String userEmail = scorecard.getUser_email();
            UserEntity user = userMap.get(userEmail);
            
            if (user == null) continue;
            
            String division = user.getDivisionName() != null ? user.getDivisionName() : "Unknown";
            String grade = user.getGrade() != null ? user.getGrade() : "Unknown";
            
            // Initialize division stats
            if (!divisionStats.containsKey(division)) {
                divisionStats.put(division, new HashMap<>());
                divisionStats.get(division).put("total", 0);
                divisionStats.get(division).put("submitted", 0);
                divisionStats.get(division).put("pending", 0);
            }
            
            // Initialize grade stats
            if (!gradeStats.containsKey(grade)) {
                gradeStats.put(grade, new HashMap<>());
                gradeStats.get(grade).put("total", 0);
                gradeStats.get(grade).put("submitted", 0);
                gradeStats.get(grade).put("pending", 0);
            }
            
            divisionStats.get(division).put("total", divisionStats.get(division).get("total") + 1);
            gradeStats.get(grade).put("total", gradeStats.get(grade).get("total") + 1);
            
            // Check if scorecard has been submitted (has a status)
            String scorecardStatus = scorecard.getScorecardStatus();
            boolean isSubmitted = scorecardStatus != null && 
                ("Approved".equals(scorecardStatus) || "ResultsScorecard".equals(scorecardStatus));
            
            if (isSubmitted) {
                submitted++;
                divisionStats.get(division).put("submitted", divisionStats.get(division).get("submitted") + 1);
                gradeStats.get(grade).put("submitted", gradeStats.get(grade).get("submitted") + 1);
                
                // Check if forwarded to HC
                if (scorecard.getForwardedToHC() != null && scorecard.getForwardedToHC()) {
                    forwardedToHC++;
                }
                
                // Check if post quarter end
                if (scorecard.getSubmittedAfterQuarterEnd() != null && scorecard.getSubmittedAfterQuarterEnd()) {
                    postQuarterEnd++;
                }
                
                // Calculate score distribution based on total score
                double totalScore = scorecard.getTotal_overal_weighted_score();
                if (totalScore >= 90) {
                    score6++;
                } else if (totalScore >= 80) {
                    score5++;
                } else if (totalScore >= 70) {
                    score4++;
                } else if (totalScore >= 60) {
                    score3++;
                } else if (totalScore >= 50) {
                    score2++;
                } else {
                    score1++;
                }
                
                // Add to performers lists
                Map<String, Object> performer = new HashMap<>();
                performer.put("username", userEmail);
                performer.put("name", user.getName());
                performer.put("surname", user.getSurname());
                performer.put("divisionName", division);
                performer.put("positionName", user.getPositionName());
                performer.put("totalScore", totalScore);
                performer.put("submissionDate", scorecard.getDateSubmitted());
                
                topPerformers.add(performer);
                bottomPerformers.add(performer);
                
            } else {
                pending++;
                divisionStats.get(division).put("pending", divisionStats.get(division).get("pending") + 1);
                gradeStats.get(grade).put("pending", gradeStats.get(grade).get("pending") + 1);
            }
        }
        
        // Sort performers by score
        topPerformers.sort((a, b) -> Double.compare((Double) b.get("totalScore"), (Double) a.get("totalScore")));
        bottomPerformers.sort((a, b) -> Double.compare((Double) a.get("totalScore"), (Double) b.get("totalScore")));
        
        // Get recent submissions
        List<Map<String, Object>> recentSubmissions = new ArrayList<>();
        for (Scorecard scorecard : allScorecards) {
            if (scorecard.getDateSubmitted() != null && "Approved".equals(scorecard.getScorecardStatus())) {
                String userEmail = scorecard.getUser_email();
                UserEntity user = userMap.get(userEmail);
                if (user != null) {
                    Map<String, Object> submission = new HashMap<>();
                    submission.put("username", userEmail);
                    submission.put("name", user.getName());
                    submission.put("surname", user.getSurname());
                    submission.put("divisionName", user.getDivisionName());
                    submission.put("submissionDate", scorecard.getDateSubmitted());
                    recentSubmissions.add(submission);
                }
            }
        }
        recentSubmissions.sort((a, b) -> {
            LocalDateTime dateA = (LocalDateTime) a.get("submissionDate");
            LocalDateTime dateB = (LocalDateTime) b.get("submissionDate");
            return dateB.compareTo(dateA);
        });
        
        // Build user-scorecard mapping for frontend users list
        List<Map<String, Object>> userScorecardList = new ArrayList<>();
        for (UserEntity user : allUsers) {
            Map<String, Object> userScorecardInfo = new HashMap<>();
            userScorecardInfo.put("username", user.getUsername());
            userScorecardInfo.put("name", user.getName());
            userScorecardInfo.put("surname", user.getSurname());
            userScorecardInfo.put("grade", user.getGrade());
            userScorecardInfo.put("divisionName", user.getDivisionName());
            userScorecardInfo.put("sectionName", user.getSectionName());
            userScorecardInfo.put("positionName", user.getPositionName());
            userScorecardInfo.put("email", user.getEmail());
            userScorecardInfo.put("ec_number", user.getEc_number());
            
            // Find matching scorecard for this user
            Scorecard userScorecard = null;
            for (Scorecard sc : allScorecards) {
                if (sc.getUser_email() != null && sc.getUser_email().equals(user.getUsername())) {
                    userScorecard = sc;
                    break;
                }
            }
            
            if (userScorecard != null) {
                String scorecardStatus = userScorecard.getScorecardStatus();
                boolean isSubmitted = scorecardStatus != null && 
                    ("Approved".equals(scorecardStatus) || "ResultsScorecard".equals(scorecardStatus));
                
                userScorecardInfo.put("hasSubmittedScorecard", isSubmitted);
                userScorecardInfo.put("submissionDate", userScorecard.getDateSubmitted());
                userScorecardInfo.put("submittedAfterQuarterEnd", userScorecard.getSubmittedAfterQuarterEnd());
                userScorecardInfo.put("scorecardStatus", scorecardStatus);
                userScorecardInfo.put("scorecardId", userScorecard.getId());
                userScorecardInfo.put("totalScore", userScorecard.getTotal_overal_weighted_score());
                userScorecardInfo.put("forwardedToHC", userScorecard.getForwardedToHC());
            } else {
                userScorecardInfo.put("hasSubmittedScorecard", false);
                userScorecardInfo.put("submissionDate", null);
                userScorecardInfo.put("submittedAfterQuarterEnd", false);
                userScorecardInfo.put("scorecardStatus", null);
                userScorecardInfo.put("scorecardId", null);
                userScorecardInfo.put("totalScore", null);
                userScorecardInfo.put("forwardedToHC", false);
            }
            
            userScorecardList.add(userScorecardInfo);
        }
        
        // Calculate compliance rate
        int complianceRate = totalUsers > 0 ? Math.round((float) submitted / totalUsers * 100) : 0;
        
        // Set all stats
        stats.put("totalUsers", totalUsers);
        stats.put("submitted", submitted);
        stats.put("pending", pending);
        stats.put("postQuarterEnd", postQuarterEnd);
        stats.put("forwardedToHC", forwardedToHC);
        stats.put("complianceRate", complianceRate);
        stats.put("divisionStats", divisionStats);
        stats.put("gradeStats", gradeStats);
        stats.put("scoreDistribution", Map.of(
            "score6", score6,
            "score5", score5,
            "score4", score4,
            "score3", score3,
            "score2", score2,
            "score1", score1
        ));
        stats.put("topPerformers", topPerformers.stream().limit(5).collect(Collectors.toList()));
        stats.put("bottomPerformers", bottomPerformers.stream().limit(5).collect(Collectors.toList()));
        stats.put("recentSubmissions", recentSubmissions.stream().limit(10).collect(Collectors.toList()));
        stats.put("userScorecardList", userScorecardList);
        
        return stats;
    }
}