package com.innovation.workplan.CollectionModels;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleSelectionRequest {
    private String username;
    private String role;
}
