package com.innovation.workplan.CollectionModels;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;


@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Document(collection = "workplans_tbl")
@JsonInclude(JsonInclude.Include.NON_NULL)

public class Workplan extends IRBMBase{
    @Transient
    public static final String SEQUENCE_NAME = "workplan_sequence";
    @Id
    long id;

    @NonNull
    private String user_email;

    // your superior's appraiser is your evaluator
    @NonNull
    private String evaluator_email;
    @NonNull
    private String appraiser_email;

    @NonNull
     private String evaluationPeriod;

     private List<WorkplanPerformanceArea> AreasOfPerformance;

    //     private List<Summary> summaryList;
// private float overalscore;
    @NonNull
    private String workplanStatus;

    private String statusComments;
    private String EvaluatorStatusComments;

    //ammended or initial (when updating save a new record)
    // approve on the employee can only be visible when the superior approves others save/submit are only visible

    //private String appraiserSignature;
    //private String appraiseeSignature;
    //private String EvaluatorSignature;


}
