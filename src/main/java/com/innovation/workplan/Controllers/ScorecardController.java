package com.innovation.workplan.Controllers;

import com.innovation.workplan.CollectionModels.Scorecard;
import com.innovation.workplan.CollectionModels.Workplan;
import com.innovation.workplan.Services.ScorecardService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/scorecard")
public class ScorecardController {

    @Autowired
    private ScorecardService scorecardService;


    @PostMapping(value= "/saveScorecard")
    @PreAuthorize("hasAnyAuthority('SAVE_SCORECARD')")
    public Long saveScorecard(@RequestBody Scorecard scorecard){
        return scorecardService.saveScorecard(scorecard);
    }


    @GetMapping("/searchScorecard")
    @PreAuthorize("hasAnyAuthority('SEARCH_SCORECARD')")
    public Page<Scorecard> searchScorecard(
            @RequestParam (required=false) Long id,
            @RequestParam (required=false) String username,
            @RequestParam (required=false) String evaluation_period,

            @RequestParam (defaultValue="0") Integer page,
            @RequestParam (defaultValue="5") Integer size){
        Pageable pageable= PageRequest.of(page,size);
        return scorecardService.searchScorecard(id,username,evaluation_period,pageable);
    }

    @GetMapping("/searchScorecardByAppraisee")
    @PreAuthorize("hasAnyAuthority('SEARCH_SCORECARD_BY_APPRAISEE')")
    @Operation(summary = "searching scorecard in the database")
    public Scorecard SearchScorecardByAppraisee(@RequestParam (required=true) String User_email,
                                              @RequestParam (required=true) String period,
                                              @RequestParam (required=true)String scorecardStatus){

        return (this.scorecardService.searchUserScorecard(User_email,period,scorecardStatus));
    }

    @PutMapping("/updateScorecard/{id}")
    @PreAuthorize("hasAnyAuthority('UPDATE_SCORECARD')")
    @Operation(summary = "updating a Scorecard in the database")
    public void updateScorecard(@PathVariable Long id, @RequestBody Scorecard scorecard){
        scorecard.setId(id);
       this.scorecardService.update(scorecard);
    }

    @RequestMapping(value = "/ScorecardReport", method = RequestMethod.GET)
    @ResponseBody
    @PreAuthorize("hasAnyAuthority('SHOW_SCORECARD_PDF')")
    public Scorecard showScorecardPDF(Principal principal){
        return scorecardService.exportScorecardPDF(principal);
    }
    @GetMapping("/searchScorecardByEvaluator")
    @PreAuthorize("hasAnyAuthority('SEARCH_SCORECARD_BY_EVALUATOR')")
    @Operation(summary = "searching a Scorecard in the database")
    public List<Scorecard> SearchScorecardByEvaluator(@RequestParam (required=true) String evaluator_email,
                                                    @RequestParam (required=true) String period,
                                                    @RequestParam (required=true) String scorecardStatus){

        return (this.scorecardService.searchScorecardByEvaluator(evaluator_email,period,scorecardStatus));
    }
    @GetMapping("/searchScorecardByStatus")
    @PreAuthorize("hasAnyAuthority('SEARCH_SCORECARD_BY_STATUS')")
    @Operation(summary = "searching a scorecards in the database")
    public List<Scorecard> SearchWorkplanByStatus(@RequestParam (required=true) String scorecardStatus){
        return (this.scorecardService.searchScorecardByStatus(scorecardStatus));
    }

    // Board-specific endpoints for grade 0 users to manage grade 1 scorecards
    @GetMapping("/searchScorecardForBoard")
    @PreAuthorize("hasAnyAuthority('SEARCH_SCORECARD', 'APPROVE_SCORECARD')")
    @Operation(summary = "Search scorecards from grade 1 users for Board approval")
    public List<Scorecard> searchScorecardForBoard(
            @RequestParam(required = false) String period,
            @RequestParam(required = false) String scorecardStatus) {
        return this.scorecardService.searchScorecardForBoard(period, scorecardStatus);
    }

    @GetMapping("/approveBoardScorecard/{id}")
    @PreAuthorize("hasAnyAuthority('UPDATE_SCORECARD')")
    @Operation(summary = "Approve a scorecard at Board level")
    public String approveBoardScorecard(
            @PathVariable Long id,
            @RequestParam(required = true) String boardMemberEmail) {
        return this.scorecardService.approveBoardScorecard(id, boardMemberEmail);
    }

    @GetMapping("/rejectBoardScorecard/{id}")
    @PreAuthorize("hasAnyAuthority('UPDATE_SCORECARD')")
    @Operation(summary = "Reject a scorecard at Board level with comments")
    public String rejectBoardScorecard(
            @PathVariable Long id,
            @RequestParam(required = true) String boardMemberEmail,
            @RequestParam(required = true) String rejectionReason) {
        return this.scorecardService.rejectBoardScorecard(id, boardMemberEmail, rejectionReason);
    }

    // Appraisee Confirmation Endpoint
    @PutMapping("/{id}/confirm")
    @PreAuthorize("hasAnyAuthority('CONFIRM_SCORECARD')")
    @Operation(summary = "Appraisee confirms acceptance of scorecard after appraiser approval")
    public String confirmScorecard(
            @PathVariable Long id,
            @RequestBody Scorecard scorecard) {
        return this.scorecardService.confirmScorecard(id, scorecard);
    }

    // HC Dashboard Endpoints
    @GetMapping("/searchAllScorecards")
    @PreAuthorize("hasAnyAuthority('HC_ACCESS', 'ADMIN')")
    @Operation(summary = "Search all scorecards for HC dashboard")
    public List<Scorecard> searchAllScorecards(
            @RequestParam(required = false) String period,
            @RequestParam(required = false) String scorecardStatus,
            @RequestParam(required = false) String grade,
            @RequestParam(required = false) String division,
            @RequestParam(required = false) String section) {
        return this.scorecardService.searchAllScorecards(period, scorecardStatus, grade, division, section);
    }

    @GetMapping("/hc/summary")
    @PreAuthorize("hasAnyAuthority('HC_ACCESS', 'ADMIN')")
    @Operation(summary = "Get HC dashboard summary statistics")
    public Map<String, Object> getHCSummary(
            @RequestParam(required = false) String period) {
        return this.scorecardService.getHCSummary(period);
    }

}
