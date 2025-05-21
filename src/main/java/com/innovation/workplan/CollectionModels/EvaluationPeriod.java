package com.innovation.workplan.CollectionModels;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@Document(collection = "EvaluationPeriods_tbl")
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)

public class EvaluationPeriod {
    @Id
    private String period;
    private int year;
    private Quarter quarter;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String periodStatus;

}
