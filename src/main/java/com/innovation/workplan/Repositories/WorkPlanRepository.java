package com.innovation.workplan.Repositories;

import com.innovation.workplan.CollectionModels.UserEntity;
import com.innovation.workplan.CollectionModels.Workplan;
//import net.sf.jasperreports.engine.JRException;
//import net.sf.jasperreports.engine.JasperPrint;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

@Repository

public interface WorkPlanRepository extends MongoRepository<Workplan,Long> {

//    Optional<Workplan> findByuser_email(String email);
//    Optional<Workplan> findByappraiser_email(String email);


}
