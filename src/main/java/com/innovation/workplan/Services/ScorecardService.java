package com.innovation.workplan.Services;

import com.innovation.workplan.CollectionModels.Scorecard;
import com.innovation.workplan.CollectionModels.Workplan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.security.Principal;
import java.util.List;

public interface ScorecardService {
    Long saveScorecard(Scorecard scorecard);
    public Scorecard searchUserScorecard(String User_email, String period, String planStatus);

    Page<Scorecard> searchScorecard(Long id, String username, String period, Pageable pageable);

    String update(Scorecard scorecard);

    Scorecard exportScorecardPDF(Principal principal);
    List<Scorecard> searchScorecardByEvaluator(String evaluator_email, String period, String planStatus);

    List<Scorecard> searchScorecardByStatus(String scorecardStatus);
}
