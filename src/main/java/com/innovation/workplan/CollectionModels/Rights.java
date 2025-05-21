package com.innovation.workplan.CollectionModels;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@Document(collection = "rights")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Rights {
    @Id
    Long id;

    String right;
}
