package com.innovation.workplan.CollectionModels;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class ScorecardMPI {

    private String description;
    private String measurement_unit;
    private String incremental_or_decremental;

    private int weight;

    private String quarterly_target;

    private String annual_target;
    private String Previous_year_Perfomenace;
    private String allowable_variance;

    private int appraisee_actual_perfomance;
    private int appraisor_actual_perfomance;
    private float agreedWeightedScore;

    private String responsibleDivision;
    private String responsibleSection;
    private List<ResponsiblePerson> responsibleResources;

    private String perfomanceComment;
    private List<String> activities;

    private List<EvidenceFields> evidenceFileIds;
    private int appraiseeScore;
}