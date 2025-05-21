package com.innovation.workplan.Controllers;

import com.innovation.workplan.CollectionModels.Signature;
import com.innovation.workplan.Services.SignatureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@CrossOrigin("*")
@RequestMapping("signature")
public class SignatureController {

    @Autowired
    private SignatureService fileService;


    @PostMapping("/upload")
    @PreAuthorize("hasAnyAuthority('UPLOAD_SIGNATURE')")
    public ResponseEntity<?> upload(@RequestParam("file")MultipartFile file) throws IOException {
        return new ResponseEntity<>(fileService.addFile(file), HttpStatus.OK);
    }


    @GetMapping("/download/{id}")
    @PreAuthorize("hasAnyAuthority('DOWNLOAD_SIGNATURE')")
    public ResponseEntity<ByteArrayResource> download(@PathVariable String id) throws IOException {
        Signature loadFile = fileService.downloadFile(id);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(loadFile.getFileType() ))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + loadFile.getFilename() + "\"")
                .body(new ByteArrayResource(loadFile.getFile()));
    }

}



