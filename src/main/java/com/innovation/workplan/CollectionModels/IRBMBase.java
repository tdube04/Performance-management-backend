package com.innovation.workplan.CollectionModels;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.lang.Nullable;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor



public class IRBMBase extends Base{
    @Nullable
    private String approved_or_rejected_By;
    @Nullable
    private LocalDateTime approved_or_rejected_Time;

    public IRBMBase(LocalDateTime created,LocalDateTime lastUpdated,String createdBy,String updatedBy
            ,String approved_or_rejected_By,LocalDateTime approved_or_rejected_Time){
        super(created,lastUpdated,createdBy,updatedBy);
        this.approved_or_rejected_By=approved_or_rejected_By;
        this.approved_or_rejected_Time=approved_or_rejected_Time;
    }

}
