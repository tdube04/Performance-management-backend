package com.innovation.workplan.CollectionModels;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.ArrayList;

@Data

@Builder
@Document(collection = "pillars")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Pillars  extends Base{
    @Transient
    public static final String SEQUENCE_NAME = "pillars_sequence";
    @Id
    Long id;

    LocalDate current_date = LocalDate.now();
    String Pillar;
    boolean active;
    String Description;
}
