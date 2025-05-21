package com.innovation.workplan.CollectionModels;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;

import java.util.ArrayList;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class ScorecardPerformanceArea {
    @Transient
    public static final String SEQUENCE_NAME = "performance_sequence";
    @Id
    Long id;

    String PerformanceArea;

    String section;
    String Description;
    int percent;
    int weight;
    ArrayList<ScorecardKPI> programs;
    float performance_area_score;
}
