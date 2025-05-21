package com.innovation.workplan.Repositories;


import com.innovation.workplan.CollectionModels.MeasurementUnits;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface MeasurementUnitsRepository extends MongoRepository<MeasurementUnits,String> {
}
