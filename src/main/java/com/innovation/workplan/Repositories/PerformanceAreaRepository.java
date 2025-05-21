package com.innovation.workplan.Repositories;

import com.innovation.workplan.CollectionModels.PerformanceArea;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PerformanceAreaRepository extends MongoRepository<PerformanceArea,Long> {

}
