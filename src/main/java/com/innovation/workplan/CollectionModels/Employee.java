package com.innovation.workplan.CollectionModels;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Employee {

    private String ecNumber;
    private String name;

   private Position position;

    private String Email;

    private String signature;
    private String signatureStatus;
}
