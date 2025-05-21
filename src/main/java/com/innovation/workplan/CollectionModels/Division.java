package com.innovation.workplan.CollectionModels;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;


@Builder
@Document(collection = "divisions")
@NoArgsConstructor
@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Division  extends Base{
    @Transient
    public static final String SEQUENCE_NAME = "division_sequence";
    @Id
    long id;

    String divisionName;
    String description;
    boolean active;
    ArrayList<String> sectionName;
}
