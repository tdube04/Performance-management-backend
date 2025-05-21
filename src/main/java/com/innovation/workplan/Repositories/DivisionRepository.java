package com.innovation.workplan.Repositories;

import com.innovation.workplan.CollectionModels.Division;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DivisionRepository extends MongoRepository<Division,Long> {
}
