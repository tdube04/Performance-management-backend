package com.innovation.workplan.CollectionModels;

import lombok.Builder;
import lombok.Data;

@Data
@Builder

public class Appraiser {
    private String firstName;

    private String lastName;
    private String ecNumber;

    private Position position;

    private String email;

    private String signature;

    private String SignatureStatus;
}



