package com.innovation.workplan.Repositories;

import com.innovation.workplan.CollectionModels.EvaluationPeriod;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface EvaluationPeriodRepository  extends MongoRepository<EvaluationPeriod,String> {
}
