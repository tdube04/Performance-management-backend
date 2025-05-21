package com.innovation.workplan.Controllers;

import com.innovation.workplan.CollectionModels.Division;
import com.innovation.workplan.Services.DivisionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/division")
public class DivisionController {

    @Autowired
    DivisionService divisionService;

   @PostMapping(value = "/save")
   @PreAuthorize("hasAnyAuthority('SAVE_DIVISION')")
    public ResponseEntity<String> saveDivision(@RequestBody Division division){
        return divisionService.saveDivision(division);
    }


    @GetMapping(value = "/allDivisions")
    @PreAuthorize("hasAnyAuthority('VIEW_ALL_DIVISION')")
    public ResponseEntity<List<Division>> getAllDivisions() {
        return divisionService.getAll();
    }

    @GetMapping(value = "/allActiveDivisions")
    @PreAuthorize("hasAnyAuthority('VIEW_ALL_ACTIVE_DIVISIONS')")
    public ResponseEntity<List<Division>> getActiveDivisions(){
       return divisionService.getActiveDivisions();
    }

    @DeleteMapping(value = "/{Id}")
    @PreAuthorize("hasAnyAuthority('DELETE_DIVISION')")
    public ResponseEntity<String> deleteDivision(@PathVariable Long Id) {
        return divisionService.deleteDivision(Id);
    }

    @PostMapping(value = "/update/{Id}")
    @PreAuthorize("hasAnyAuthority('UPDATE_DIVISION')")
    public ResponseEntity<String> updateDivision(@PathVariable Long Id, @RequestBody Division division){
       division.setId(Id);
       return divisionService.updateDivision(division);
    }

    @PostMapping(value = "/deactivateDivision")
    @PreAuthorize("hasAnyAuthority('DEACTIVATE_DIVISION')")
    public ResponseEntity<String> deactivateDivision(String division){
       return divisionService.deactivateDivision(division);
    }

    @PostMapping(value = "/addSection")
    @PreAuthorize("hasAnyAuthority('ADD_SECTION')")
    public ResponseEntity<String> addSection(String sectionName, String divisionName){
        return divisionService.addSection(sectionName, divisionName);
    }

    @PutMapping(value = "/updateSection/{divisionName}")
    @PreAuthorize("hasAnyAuthority('ADD_SECTION')")
    public ResponseEntity<String> updateSection(@PathVariable String divisionName, String oldSectionName, String newSectionName){
       return divisionService.updateSection(divisionName, oldSectionName, newSectionName);
    }

    @DeleteMapping(value = "/deleteS/{division}")
    @PreAuthorize("hasAnyAuthority('DELETE_SECTION')")
    public ResponseEntity<String> deleteSection(@PathVariable String division, String sectionName){
        return divisionService.deleteSection(division, sectionName);
    }
}
