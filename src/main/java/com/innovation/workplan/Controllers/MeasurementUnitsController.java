package com.innovation.workplan.Controllers;

import com.innovation.workplan.CollectionModels.Division;
import com.innovation.workplan.CollectionModels.MeasurementUnits;
import com.innovation.workplan.Services.DivisionService;
import com.innovation.workplan.Services.MeasurementUnitsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping(value = "/MeasurementUnits")
public class MeasurementUnitsController {



        @Autowired
        MeasurementUnitsService measurementUnitsService;

        @PostMapping(value = "/save")
        @PreAuthorize("hasAnyAuthority('SAVE_UNIT_OF_MEASURE')")
        public String saveMeasurementUnit(@RequestBody MeasurementUnits unit){
            return measurementUnitsService.saveMeasurementUnits(unit);
        }


        @GetMapping(value = "/allUnits")
        @PreAuthorize("hasAnyAuthority('VIEW_ALL_UNITS_OF_MEASURE')")
        public List<MeasurementUnits> getAllMeasurementUnits() {
            return measurementUnitsService.getAllMeasurementUnits();
        }

        @GetMapping(value = "suspend/{Id}")
        @PreAuthorize("hasAnyAuthority('DELETE_UNIT')")
        public String deleteUnit(@PathVariable String Id) {
             measurementUnitsService.deleteMeasurementUnits(Id);
             return "temporarily deleted"+"\n"+Id;
        }

    @GetMapping(value = "delete/{Id}")
    @PreAuthorize("hasAnyAuthority('DELETE_UNIT_PERMANENTLY')")
    public String PermanentlydeleteUnit(@PathVariable String Id) {
        measurementUnitsService.permanentlyDeleteMeasurementUnits(Id);
        return "permanently deleted"+"\n"+Id;
    }


    @PostMapping(value = "/updateUnit")
        @PreAuthorize("hasAnyAuthority('UPDATE_UNIT')")
        public MeasurementUnits updateUnit(@RequestBody MeasurementUnits measurementUnits){
            return measurementUnitsService.updateMeasurementUnits(measurementUnits);
        }

    @PostMapping(value = "/findUnit/{Id}")
    @PreAuthorize("hasAnyAuthority('FIND_UNIT')")
    public MeasurementUnits findUnit(@RequestParam String id){
        return measurementUnitsService.findMeasurementUnitById(id);
    }



}
