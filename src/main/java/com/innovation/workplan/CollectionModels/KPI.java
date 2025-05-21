package com.innovation.workplan.CollectionModels;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class KPI {

   // private String nds1_pillar;
    private String Name;
    private String contributedPillar;
    private int Weight;
    private List<MPI> indicators;
}
