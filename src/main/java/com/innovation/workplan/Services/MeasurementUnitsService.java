package com.innovation.workplan.Services;

import com.innovation.workplan.CollectionModels.Division;
import com.innovation.workplan.CollectionModels.MeasurementUnits;

import java.util.List;

public interface MeasurementUnitsService {
    String saveMeasurementUnits(MeasurementUnits measurementUnit);
    List<MeasurementUnits> getAllMeasurementUnits();
    MeasurementUnits updateMeasurementUnits(MeasurementUnits measurementUnit);
    void permanentlyDeleteMeasurementUnits(String Id);

    void deleteMeasurementUnits(String Id);
    MeasurementUnits findMeasurementUnitById(String Id);


}
