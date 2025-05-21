package com.innovation.workplan.CollectionModels;

import lombok.Builder;
import lombok.Data;

@Data
@Builder

public class Summary {
    private String section;
    private String description;
    private int weight;

    private float weightedScore;


}
