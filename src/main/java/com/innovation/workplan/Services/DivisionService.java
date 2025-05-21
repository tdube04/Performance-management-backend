package com.innovation.workplan.Services;

import com.innovation.workplan.CollectionModels.Division;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface DivisionService {

    ResponseEntity<String> saveDivision(Division division);
    ResponseEntity<List<Division>> getAll();
    ResponseEntity<String> updateDivision(Division division);
    ResponseEntity<String> deleteDivision(Long Id);
    ResponseEntity<String> deleteSection(String division, String section);
    ResponseEntity<String> addSection(String sectionName, String divisionName);
    ResponseEntity<String> updateSection(String divisionName, String oldSectionName, String newSectionName);
    ResponseEntity<List<Division>> getActiveDivisions();
    ResponseEntity<String> deactivateDivision(String division);
}
