package com.innovation.workplan.Services;

import com.innovation.workplan.CollectionModels.Percentage;
import com.innovation.workplan.CollectionModels.PerformanceArea;
import org.springframework.http.ResponseEntity;

public interface PercentageService {

     ResponseEntity<String> savePercentage(PerformanceArea performanceArea);
}
