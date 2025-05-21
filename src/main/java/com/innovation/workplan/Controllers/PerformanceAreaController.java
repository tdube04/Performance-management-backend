package com.innovation.workplan.Controllers;

import com.innovation.workplan.CollectionModels.AdminProgram;
import com.innovation.workplan.CollectionModels.KPI;
import com.innovation.workplan.CollectionModels.PerformanceArea;
import com.innovation.workplan.Services.PerformanceAreaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/Performance_Area")
public class PerformanceAreaController {

    @Autowired
    PerformanceAreaService performanceAreaService;

    @PostMapping(value = "/save")
    @PreAuthorize("hasAnyAuthority('SAVE_PERFORMANCE_AREA')")
    public ResponseEntity<String> savePerformanceArea(@RequestBody PerformanceArea performanceArea){
        return performanceAreaService.savePerformanceArea(performanceArea);
    }


    @GetMapping(value = "/allAreas")
    @PreAuthorize("hasAnyAuthority('VIEW_ALL_PERFORMANCE_AREA')")
    public ResponseEntity<List<PerformanceArea>> getAllPerformanceAreas() {

        return performanceAreaService.getAll();
    }

    @PostMapping(value = "/{Id}")
    @PreAuthorize("hasAnyAuthority('DELETE_PERFORMANCE_AREA')")
    public ResponseEntity<String> deletePerformanceArea(@PathVariable Long Id) {
       return performanceAreaService.deletePerformanceArea(Id);
    }

    @PostMapping(value = "/update/{id}")
    @PreAuthorize("hasAnyAuthority('UPDATE_PERFORMANCE_AREA')")
    public ResponseEntity<String> updatePerformanceArea(@PathVariable Long id, @RequestBody PerformanceArea performanceArea){
        performanceArea.setId(id);
       return performanceAreaService.updatePerformanceArea(performanceArea);
    }

    @PostMapping(value = "/deactivatePerformanceArea")
    @PreAuthorize("hasAnyAuthority('DEACTIVATE_PERFORMANCE_AREA')")
    public ResponseEntity<String> deactivatePerformanceArea(String performanceArea){
        return performanceAreaService.deactivatePerformanceArea(performanceArea);
    }

    @PostMapping(value = "/deletePrograms/{program}")
    @PreAuthorize("hasAnyAuthority('DELETE_PROGRAM')")
    public ResponseEntity<String> deleteProgram(@PathVariable String program,@RequestBody AdminProgram programs){
        programs.setProgramName(program);
        return performanceAreaService.deleteProgram(programs);
    }

    @PostMapping(value = "/addProgram/{performanceArea}")
    @PreAuthorize("hasAnyAuthority('ADD_PROGRAM')")
    public ResponseEntity<String> addProgram(@RequestBody AdminProgram program, @PathVariable String performanceArea){
        return performanceAreaService.addProgram(program, performanceArea);
    }

    @GetMapping(value = "/findActivePerformanceArea")
    @PreAuthorize("hasAnyAuthority('FIND_ACTIVE_PERFORMANCE_AREA')")
    public ResponseEntity<List<PerformanceArea>> getActivePerformanceAreas(){
        return performanceAreaService.getActivePerformanceAreas();
    }

    @GetMapping(value = "/findByPerformanceName/{performanceArea}")
    @PreAuthorize("hasAnyAuthority('FIND_PERFORMANCE_AREA')")
    public ResponseEntity<PerformanceArea> findByPerformanceArea(@PathVariable String performanceArea){
        return performanceAreaService.getByPerformanceArea(performanceArea);
    }

    @GetMapping(value = "/findByYear/{year}")
    @PreAuthorize("hasAnyAuthority('FIND_PERFORMANCE_AREA')")
    public ResponseEntity<PerformanceArea> getByYear(@PathVariable int year){
        return  performanceAreaService.getByYear(year);
    }

}
