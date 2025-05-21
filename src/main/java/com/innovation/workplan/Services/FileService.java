package com.innovation.workplan.Services;

import com.innovation.workplan.CollectionModels.LoadFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileService {
    Object addFile(MultipartFile file) throws IOException;

    LoadFile downloadFile(String id) throws IOException;
}
