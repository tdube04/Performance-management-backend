package com.innovation.workplan.Controllers;

import com.innovation.workplan.CollectionModels.AccountingRatios;

import com.innovation.workplan.Services.AccountingRatiosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/ratios")

public class AccountingRatiosContoller {

    @Autowired
    private AccountingRatiosService accountingRatiosService;
    @PostMapping(value = "/save")
    @PreAuthorize("hasAnyAuthority('SAVE_RATIO')")
    public ResponseEntity<String> saveRatio(@RequestBody AccountingRatios ratio){
        return accountingRatiosService.saveRatio(ratio);
    }


    @GetMapping(value = "/allRatios")
    @PreAuthorize("hasAnyAuthority('VIEW_ALL_RATIOS')")
    public ResponseEntity<List<AccountingRatios>> getAllRatios() {
        return accountingRatiosService.getAllratios();
    }



    @DeleteMapping(value = "/{Id}")
    @PreAuthorize("hasAnyAuthority('DELETE_RATIO')")
    public ResponseEntity<String> deleteRatio(@PathVariable Long Id) {
        return accountingRatiosService.delete(Id);
    }



    @PostMapping(value = "/update/{Id}")
    @PreAuthorize("hasAnyAuthority('UPDATE_RATIO')")
    public ResponseEntity<String> updateRatio(@PathVariable Long Id, @RequestBody AccountingRatios ratio){
        ratio.setId(Id);
        return accountingRatiosService.updateRatio(ratio);
    }


}
