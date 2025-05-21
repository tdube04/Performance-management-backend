package com.innovation.workplan.CollectionModels;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "percentage")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Percentage {

    @Transient
    public static final String SEQUENCE_NAME = "percentage_sequence";
    @Id
    Long id;

    int percent;
}
