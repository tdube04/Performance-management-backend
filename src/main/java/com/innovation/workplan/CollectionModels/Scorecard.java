package com.innovation.workplan.CollectionModels;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "scorecards_tbl")

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"id","employee","evaluator","appraiser","evaluationPeriod","AreasOfPerformnce","summaryList","overalscore",
        "scorecardStatus"
})

public class Scorecard extends IRBMBase {
    @Transient
    public static final String SEQUENCE_NAME = "scorecard_sequence";
    @Id
    long id;
    @NonNull
    private String user_email;
//    @NonNull
    private String evaluator;
    @NonNull
    private String appraiser;
    @NonNull
    private String evaluationPeriod;
    private List<ScorecardPerformanceArea> AreasOfPerformance;
    private List<Summary> summaryList;
    private float total_overal_weighted_score;
    private String scorecardStatus;
    private String scorecardStatusComment;
    //working or results (when updating save a new record)
    // approve on the employee can only be visible when the superior approves others save/submit are only visible




}
