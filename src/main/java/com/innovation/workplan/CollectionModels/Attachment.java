package com.innovation.workplan.CollectionModels;

import lombok.Data;

@Data


public class Attachment {

    private String file_name;
    private String file_type;

    private byte[] file_data;
}
