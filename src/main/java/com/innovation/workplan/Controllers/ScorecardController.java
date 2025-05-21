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

}
