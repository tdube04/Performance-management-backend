package com.innovation.workplan.CollectionModels;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class MPI {

   private String description;
    private String measurement_unit;
    private String incremental_or_decremental;
    private Integer weight;
    private int quarterly_target;
    private int annual_target;
    private int Previous_year_Perfomenace;
    private int allowable_variance;

//    private int actual_perfomance;
//    private float agreedWeightedScore;
//
    private String responsibleDivision;
 private String responsibleSection;
 private List<ResponsiblePerson> responsibleResources;

//    private String perfomanceComment;
//
//    private List<String> evidenceFileIds;
//    private int apraiserScore;







}
