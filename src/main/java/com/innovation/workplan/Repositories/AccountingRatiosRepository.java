package com.innovation.workplan.Repositories;

import com.innovation.workplan.CollectionModels.AccountingRatios;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AccountingRatiosRepository extends MongoRepository<AccountingRatios,Long> {
}
