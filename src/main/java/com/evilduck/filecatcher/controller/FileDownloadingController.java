package com.evilduck.filecatcher.controller;

import com.evilduck.filecatcher.dto.FileUploadResponse;
import com.evilduck.filecatcher.request.JobProcessingRequest;
import com.evilduck.filecatcher.service.FileService;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.fileupload2.core.FileItemInput;
import org.apache.commons.fileupload2.core.FileItemInputIterator;
import org.apache.commons.fileupload2.jakarta.JakartaServletFileUpload;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.IOException;
import java.io.InputStream;

public abstract class FileDownloadingController {

    private final FileService fileService;

    protected FileDownloadingController(final FileService fileService) {
        this.fileService = fileService;
    }

    protected ResponseEntity<FileUploadResponse> handleUpload(final HttpServletRequest request) throws IOException {
        final boolean isMultipartContent = JakartaServletFileUpload.isMultipartContent(request);

        if (isMultipartContent) {

            final JakartaServletFileUpload upload = new JakartaServletFileUpload<>();
            final FileItemInputIterator iterator = upload.getItemIterator(request);

            while (iterator.hasNext()) {
                FileItemInput nextItem = iterator.next();
                if (!nextItem.isFormField()) {
                    final InputStream inputStream = nextItem.getInputStream();
                    final String fileName = nextItem.getName();
                    final String savedFileName = fileService.save(inputStream, fileName, nextItem.getContentType());
                    return ResponseEntity.ok(new FileUploadResponse("File Saved Successfully", savedFileName));
                }
            }
            return ResponseEntity.ok(new FileUploadResponse("No File Attached", null));
        } else {
            return ResponseEntity.badRequest().body(new FileUploadResponse("Upload is not Multipart Content", null));
        }
    }

    protected ResponseEntity<Void> processMedia(@RequestBody final JobProcessingRequest jobProcessingRequest) {
        fileService.process(jobProcessingRequest.jobIds());
        return ResponseEntity.noContent().build();
    }

}
