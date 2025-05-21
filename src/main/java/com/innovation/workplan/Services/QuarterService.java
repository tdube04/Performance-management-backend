package com.innovation.workplan.Services;

import com.innovation.workplan.CollectionModels.Quarter;

import java.util.List;

public interface QuarterService {

    public String saveQuarter(Quarter quarter);
    public String updateQuarter(Quarter quarter);

    public List<Quarter> getAllQuarters();
}
