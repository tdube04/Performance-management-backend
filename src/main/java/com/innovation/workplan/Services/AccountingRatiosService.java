package com.innovation.workplan.Services;

import com.innovation.workplan.CollectionModels.AccountingRatios;
import com.innovation.workplan.CollectionModels.RatioVariable;
import com.innovation.workplan.CollectionModels.RatiosVariablesRequest;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;


public interface AccountingRatiosService {


    ResponseEntity<String> saveRatio(AccountingRatios ratio);
    ResponseEntity<String> updateRatio(AccountingRatios ratio);
    ResponseEntity<String> delete(Long id);
    AccountingRatios getRatio(Long id);
    ResponseEntity<List<AccountingRatios>> getAllratios();

}
