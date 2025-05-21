package com.innovation.workplan.Controllers;


import com.innovation.workplan.CollectionModels.EvaluationPeriod;
import com.innovation.workplan.CollectionModels.Quarter;
import com.innovation.workplan.Services.EvaluationPeriodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/evaluation_periods")
public class EvaluationPeriodController {

    @Autowired
    private EvaluationPeriodService evaluationPeriodService;

    @PutMapping(value = "/close")
    @PreAuthorize("hasAnyAuthority('CLOSE_EVALUATION_PERIOD')")
    public String closeEvaluationPeriod(@RequestParam(value = "id", required = true) String id){

        return evaluationPeriodService.closeEvaluationPeriod(id);
    }
    @PostMapping(value = "/open")
    @PreAuthorize("hasAnyAuthority('OPEN_EVALUATION_PERIOD')")
    public String openEvaluationPeriod(@RequestParam int year, @RequestParam String quarter_name){
        return evaluationPeriodService.openEvaluationPeriod(year,quarter_name);
    }
}
