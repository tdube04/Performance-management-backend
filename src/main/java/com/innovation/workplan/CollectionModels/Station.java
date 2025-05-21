package com.innovation.workplan.CollectionModels;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Station {
    private String name;
    private String station_code;
    private String city;
}
