package com.innovation.workplan.CollectionModels;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@Document(collection = "grade")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Grade {
    @Transient
    public static final String SEQUENCE_NAME = "grade_sequence";
    @Id
    Long id;

    int Grade;
}
