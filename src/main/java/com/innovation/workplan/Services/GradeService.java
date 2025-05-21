package com.innovation.workplan.Services;

import com.innovation.workplan.CollectionModels.Grade;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface GradeService {

    ResponseEntity<String> saveGrade(@RequestBody Grade grade);

    ResponseEntity<List<Grade>> getAll();

    ResponseEntity<String> deleteGrade(Long Id);
}
