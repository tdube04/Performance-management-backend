package com.innovation.workplan.Services;

import com.innovation.workplan.CollectionModels.EvaluationPeriod;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

public interface EvaluationPeriodService {

    //if you open year you can now view all the quarters within the year and if you close year you cant view anything for any quarter in that year
    String closeYear(String year);

    String openYear(int year);
    String closeEvaluationPeriod(String id);

    String openEvaluationPeriod(int year,String quarter);
    
    List<EvaluationPeriod> getAllEvaluationPeriods();
    
    // Get current quarter status info for dashboard display
    Map<String, Object> getCurrentQuarterStatus();
    
    // Get all quarters with their status for dashboard
    Map<String, Object> getAllQuartersStatus();
}
