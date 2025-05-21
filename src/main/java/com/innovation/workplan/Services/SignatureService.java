package com.innovation.workplan.Services;

import com.innovation.workplan.CollectionModels.Signature;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface SignatureService {
    Object addFile(MultipartFile file) throws IOException;

    Signature downloadFile(String id) throws IOException;
}
