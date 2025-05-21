package com.innovation.workplan.CollectionModels;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)

public class WorkplanPerformanceArea {
//    @Transient
//    public static final String SEQUENCE_NAME = "performance_sequence";
//    @Id
//    Long id;

    String PerformanceArea;
    String section;
    String Description;
    int weight;
    ArrayList<KPI> programs;
}
