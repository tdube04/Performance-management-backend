package com.innovation.workplan.Services;

import com.innovation.workplan.CollectionModels.AdminProgram;
import com.innovation.workplan.CollectionModels.KPI;
import com.innovation.workplan.CollectionModels.PerformanceArea;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.List;

public interface PerformanceAreaService {

    ResponseEntity<String> savePerformanceArea(@RequestBody PerformanceArea performanceArea);
    ResponseEntity<List<PerformanceArea>> getAll();
    ResponseEntity<String> updatePerformanceArea(@RequestBody PerformanceArea performanceArea);
    ResponseEntity<String> deletePerformanceArea(Long Id);
    ResponseEntity<String> deleteProgram(AdminProgram program);
    ResponseEntity<String> addProgram(@RequestBody AdminProgram program, String performanceArea);
    ResponseEntity<List<PerformanceArea>> getActivePerformanceAreas();
    ResponseEntity<PerformanceArea> getByPerformanceArea(String performanceArea);
    ResponseEntity<PerformanceArea> getByYear(int year);
    ResponseEntity<String> deactivatePerformanceArea(String performanceArea);
}
