package com.innovation.workplan.Services;

import com.innovation.workplan.CollectionModels.Rights;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface RightsService {
    ResponseEntity<List<Rights>> getAllRights();

    ResponseEntity<String> saveRights(Rights rights);
}
