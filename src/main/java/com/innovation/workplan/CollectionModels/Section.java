package com.innovation.workplan.CollectionModels;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder

@Document(collection = "sections")
public class Section {

    private Integer section_id;
    private String name;
    private String division;

}
