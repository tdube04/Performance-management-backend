package com.innovation.workplan.Controllers;

import com.innovation.workplan.CollectionModels.Rights;
import com.innovation.workplan.Services.RightsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/Rights")
public class RightsController {

    @Autowired
    RightsService rightsService;

    @GetMapping(value = "/allRights")
    @PreAuthorize("hasAnyAuthority('VIEW_RIGHTS')")
    public ResponseEntity<List<Rights>> getAllRights() {

        return rightsService.getAllRights();
    }

    @PostMapping(value = "/save")
    @PreAuthorize("hasAnyAuthority('SAVE_RIGHTS')")
    public ResponseEntity<String> saveRights(@RequestBody Rights rights){

        return rightsService.saveRights(rights);
    }
}
