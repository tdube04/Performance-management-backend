package com.innovation.workplan.Services;

import com.innovation.workplan.CollectionModels.EvaluationPeriod;

public interface EvaluationPeriodService {

    //if you open year you can now view all the quarters within the year and if you close year you cant view anything for any quarter in that year
    String closeYear(String year);

    String openYear(int year);
    String closeEvaluationPeriod(String id);

    String openEvaluationPeriod(int year,String quarter);
}
