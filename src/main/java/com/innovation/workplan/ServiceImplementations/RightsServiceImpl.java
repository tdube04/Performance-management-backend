package com.innovation.workplan.ServiceImplementations;

import com.innovation.workplan.CollectionModels.Rights;
import com.innovation.workplan.Repositories.RightsRepository;
import com.innovation.workplan.Services.RightsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RightsServiceImpl implements RightsService {

    @Autowired
    private RightsRepository rightsRepository;
    @Autowired
    private MongoTemplate mongoTemplate;

    public ResponseEntity<List<Rights>> getAllRights() {

        return ResponseEntity.status(200).body(rightsRepository.findAll());
    }

    public ResponseEntity<String> saveRights(Rights rights){
        rightsRepository.save(rights);
        return ResponseEntity.status(200).body("Rights Saved");
    }

}
