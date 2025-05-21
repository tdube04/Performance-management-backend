package com.innovation.workplan.CollectionModels;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;

@Data
@Builder

public class RatiosVariablesRequest {
    ArrayList<RatioVariable> variables;
}
