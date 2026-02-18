package com.innovation.workplan.Services;

import com.innovation.workplan.CollectionModels.Scorecard;
import com.innovation.workplan.CollectionModels.Workplan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.security.Principal;
import java.util.List;
import java.util.Map;

public interface ScorecardService {
    Long saveScorecard(Scorecard scorecard);
    public Scorecard searchUserScorecard(String User_email, String period, String planStatus);

    Page<Scorecard> searchScorecard(Long id, String username, String period, Pageable pageable);

    String update(Scorecard scorecard);

    Scorecard exportScorecardPDF(Principal principal);
    List<Scorecard> searchScorecardByEvaluator(String evaluator_email, String period, String planStatus);

    List<Scorecard> searchScorecardByStatus(String scorecardStatus);

    // Board-specific methods for grade 0 users to manage grade 1 scorecards
    List<Scorecard> searchScorecardForBoard(String period, String scorecardStatus);

    String approveBoardScorecard(Long id, String boardMemberEmail);

    String rejectBoardScorecard(Long id, String boardMemberEmail, String rejectionReason);

    // Appraisee Confirmation Methods
    String confirmScorecard(Long id, Scorecard scorecard);

    // HC Dashboard Methods
    List<Scorecard> searchAllScorecards(String period, String scorecardStatus, String grade, String division, String section);

    Map<String, Object> getHCSummary(String period);

    // HC Receive Scorecard Method
    String markScorecardReceivedByHC(Long id, String hcEmail);

    // HC Dashboard comprehensive stats from scorecard data
    Map<String, Object> getHCDashboardStats(String period);
}
