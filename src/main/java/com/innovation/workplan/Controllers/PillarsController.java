package com.innovation.workplan.Controllers;

import com.innovation.workplan.CollectionModels.Pillars;
import com.innovation.workplan.Services.PillarsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/Pillars")
public class PillarsController {

    @Autowired
    PillarsService pillarsService;

    @PostMapping(value = "/save")
    @PreAuthorize("hasAnyAuthority('SAVE_PILLAR')")
    public ResponseEntity<String> savePillar(@RequestBody Pillars pillars){
        return pillarsService.savePillar(pillars);
    }

    @GetMapping(value = "/allPillars")
    @PreAuthorize("hasAnyAuthority('VIEW_ALL_PILLAR')")
    public ResponseEntity<List<Pillars>> getAllPillars() {
        return pillarsService.getAll();
    }

    @GetMapping(value = "/allActivePillars")
    @PreAuthorize("hasAnyAuthority('VIEW_ACTIVE_PILLARS')")
    public ResponseEntity<List<Pillars>> getActivePillars() {
        return pillarsService.getActivePillars();
    }

    @GetMapping(value = "/byYear")
    @PreAuthorize("hasAnyAuthority('VIEW_PILLAR')")
    public ResponseEntity<String> getAllByYear(@PathVariable int year) {

        return pillarsService.getAllByYear(year);
    }

    @PostMapping(value = "/deactivatePillar")
    @PreAuthorize("hasAnyAuthority('DEACTIVATE_PILLAR')")
    public ResponseEntity<String> deactivatePillars(String pillar){
        return pillarsService.deactivatePillars(pillar);
    }

    @GetMapping(value = "/{Id}")
    @PreAuthorize("hasAnyAuthority('DELETE_PILLAR')")
    public ResponseEntity<String> deletePillar(@PathVariable Long Id) {
        return pillarsService.deletePillar(Id);
    }

    @PostMapping(value = "/update")
    @PreAuthorize("hasAnyAuthority('UPDATE_PILLAR')")
    public ResponseEntity<String> updatePillar(@RequestBody Pillars pillars){
        return pillarsService.updatePillars(pillars);
    }
}
