package com.innovation.workplan.Repositories;

import com.innovation.workplan.CollectionModels.Grade;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GradeRepository extends MongoRepository<Grade,Long> {
}
