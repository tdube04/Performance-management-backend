package com.innovation.workplan.Repositories;


import com.innovation.workplan.CollectionModels.UserGroup;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface UserGroupRepository extends MongoRepository<UserGroup,String> {


}
