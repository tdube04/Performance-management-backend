package com.innovation.workplan.Repositories;


import com.innovation.workplan.CollectionModels.UserEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository

public interface UserEntityRepository extends MongoRepository<UserEntity,String> {

    Optional<UserEntity> findByresetToken(String token);

    Optional<UserEntity> findByUsername(String username);



}
