package com.innovation.workplan.Controllers;


import com.innovation.workplan.CollectionModels.PerformanceArea;
import com.innovation.workplan.CollectionModels.Quarter;
import com.innovation.workplan.Services.QuarterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/Quarters")
public class QuarterController {

    @Autowired
    private QuarterService quarterService;


    @PutMapping(value = "/update/{id}")
    @PreAuthorize("hasAnyAuthority('UPDATE_QUARTER')")
    public String updateQuarter(@PathVariable String id, @RequestBody Quarter quarter){
        quarter.setQuarter_name(id);
        return quarterService.updateQuarter(quarter);
    }
    @PutMapping(value = "/getAll")
    @PreAuthorize("hasAnyAuthority('GET_ALL_QUARTERS')")
    public List<Quarter> getAllQuarter(){
        return quarterService.getAllQuarters();
    }
    @PostMapping(value = "/save")
    @PreAuthorize("hasAnyAuthority('SAVE_QUARTER')")
    public String saveQuarter( @RequestBody Quarter quarter){
        return quarterService.saveQuarter(quarter);
    }
}
