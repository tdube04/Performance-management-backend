package com.innovation.workplan.Controllers;

import com.innovation.workplan.CollectionModels.Grade;
import com.innovation.workplan.Services.GradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/Grade")
public class GradeController {

    @Autowired
    GradeService gradeService;

    @PostMapping(value = "/save")
    @PreAuthorize("hasAnyAuthority('SAVE_GRADE')")
    public ResponseEntity<String> saveGrade(@RequestBody Grade grade){
        return gradeService.saveGrade(grade);
    }

    @GetMapping(value = "/allGrades")
    @PreAuthorize("hasAnyAuthority('VIEW_GRADE')")
    public ResponseEntity<List<Grade>> getAllGrades() {
        return gradeService.getAll();
    }

    @PostMapping(value = "/{Id}")
    @PreAuthorize("hasAnyAuthority('DELETE_GRADE')")
    public ResponseEntity<String> deleteGrade(@PathVariable Long Id) {
        return gradeService.deleteGrade(Id);

    }
}
