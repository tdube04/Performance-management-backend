package com.innovation.workplan.Repositories;

import com.innovation.workplan.CollectionModels.Rights;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RightsRepository extends MongoRepository<Rights,Long> {

}
