package com.evilduck.filecatcher.controller;

import com.evilduck.filecatcher.dto.FileUploadResponse;
import com.evilduck.filecatcher.request.JobProcessingRequest;
import com.evilduck.filecatcher.service.FileService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.fileupload2.jakarta.JakartaServletFileUpload;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
public abstract class FileDownloadingController {

    private static final String NUM_OF_CHUNKS_PARAM = "numOfChunks";
    private static final String CHUNK_NUMBER_PARAM = "chunkNumber";

    private final FileService fileService;

    protected FileDownloadingController(final FileService fileService) {
        this.fileService = fileService;
    }

    protected ResponseEntity<FileUploadResponse> handleUpload(final HttpServletRequest request) throws IOException, ServletException {
        final boolean isMultipartContent = JakartaServletFileUpload.isMultipartContent(request);

        if (isMultipartContent) {
            final Part filePart = request.getPart("fileChunk");
            final long totalFileBytes = Long.parseLong(request.getParameter("totalFileBytes"));
            final long chunkStartByte = Long.parseLong(request.getParameter("chunkStartByte"));
            if (filePart == null) {
                return ResponseEntity.ok(new FileUploadResponse("No File Attached", null, 0));
            } else {
                final String fileName = filePart.getSubmittedFileName();
                log.info("Received bytes starting at {} of file {}", chunkStartByte, fileName);
                final InputStream inputStream = filePart.getInputStream();
                final String savedFileName = fileService.saveOrAppend(inputStream, fileName, chunkStartByte, totalFileBytes, filePart.getContentType());
                return ResponseEntity.ok(new FileUploadResponse("File Saved Successfully", savedFileName, filePart.getSize()));
            }
        } else {
            return ResponseEntity.badRequest().body(new FileUploadResponse("Upload is not Multipart Content", null, 0));
        }
    }

    protected ResponseEntity<Void> processMedia(@RequestBody final JobProcessingRequest jobProcessingRequest) {
        fileService.process(jobProcessingRequest.jobIds());
        return ResponseEntity.noContent().build();
    }

}
