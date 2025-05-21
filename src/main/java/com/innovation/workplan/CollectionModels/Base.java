package com.innovation.workplan.CollectionModels;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.lang.Nullable;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data

public class Base {

    private LocalDateTime created;
    private LocalDateTime lastUpdated;
    @Nullable
    private String createdBy;
    private String updatedBy;






}
