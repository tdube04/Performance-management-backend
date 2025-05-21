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

import java.util.Dictionary;
import java.util.List;


@Builder
@Data
@Document(collection = "accounting_ratios")
@NoArgsConstructor
@AllArgsConstructor

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AccountingRatios  extends Base{
    @Transient
    public static final String SEQUENCE_NAME = "ratios_sequence";
    @Id
    long id;
    private String program;

    private String ratioName;

    private String increamental_decreamental;

    private String measurement_unit;
}
