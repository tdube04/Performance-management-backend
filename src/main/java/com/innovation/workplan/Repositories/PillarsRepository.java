package com.innovation.workplan.Repositories;

import com.innovation.workplan.CollectionModels.Pillars;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PillarsRepository extends MongoRepository<Pillars,Long> {
}
