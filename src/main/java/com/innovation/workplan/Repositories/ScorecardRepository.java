package com.innovation.workplan.Repositories;

import com.innovation.workplan.CollectionModels.Scorecard;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface ScorecardRepository extends MongoRepository<Scorecard,Long> {
}
