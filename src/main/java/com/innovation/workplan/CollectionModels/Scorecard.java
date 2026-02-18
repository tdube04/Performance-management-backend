package com.innovation.workplan.CollectionModels;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
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
    
    // Date tracking for scorecard submission/approval
    private LocalDateTime dateSubmitted;
    private LocalDateTime dateApproved;
    
    // Appraisee Confirmation Fields
    private Boolean appraiseeConfirmed;
    private LocalDateTime appraiseeConfirmedAt;
    private String confirmationStatus; // PENDING, CONFIRMED, REJECTED
    private String appraiseeComments;
    
    // Forward to HC Fields
    private Boolean forwardedToHC;
    private LocalDateTime forwardedToHCAt;
    
    // Status for HC workflow
    private String hcStatus; // PENDING_HC, UNDER_REVIEW_HC, COMPLETED
    
    // HC Received tracking
    private LocalDateTime hcReceivedAt;
    private String hcReceivedBy;
    
    // Quarter end tracking
    private Boolean submittedAfterQuarterEnd;
    private LocalDateTime quarterEndDate;
    
    // Timestamps
    private LocalDateTime approvedByAppraiserAt;
    private LocalDateTime approvedByEvaluatorAt;
    private LocalDateTime approvedByBoardAt;
}
