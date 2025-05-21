package com.innovation.workplan.CollectionModels;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "database_sequences")

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DatabaseSequence extends Base{
    @Id
    private String id;
    private long seq;
}
