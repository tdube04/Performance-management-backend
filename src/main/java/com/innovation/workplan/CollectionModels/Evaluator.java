package com.innovation.workplan.CollectionModels;

import lombok.Builder;
import lombok.Data;

@Data
@Builder

public class Evaluator {
    private String firstName;

   private String ecNumber;

    private String lastName;

    private Position position;

    private String Email;
}
