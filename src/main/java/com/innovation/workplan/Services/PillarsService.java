package com.innovation.workplan.Services;

import com.innovation.workplan.CollectionModels.Pillars;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface PillarsService {

    ResponseEntity<String> savePillar(@RequestBody Pillars pillars);
    ResponseEntity<List<Pillars>> getAll();
    ResponseEntity<String> updatePillars(@RequestBody Pillars pillars);
    ResponseEntity<String> deletePillar(Long Id);

    ResponseEntity<String> getAllByYear(int x);

    ResponseEntity<List<Pillars>> getActivePillars();
    ResponseEntity<String> deactivatePillars(String pillar);
}
