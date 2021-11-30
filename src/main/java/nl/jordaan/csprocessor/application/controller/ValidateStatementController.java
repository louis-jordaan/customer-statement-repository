package nl.jordaan.csprocessor.application.controller;

import nl.jordaan.csprocessor.objectmodel.ReferenceNumberGenerator;
import nl.jordaan.csprocessor.objectmodel.constant.ExecutionStatus;
import nl.jordaan.csprocessor.objectmodel.exception.StatementProcessingApiException;
import nl.jordaan.csprocessor.objectmodel.execution.ExecutionDetails;
import nl.jordaan.csprocessor.service.filestorage.IFileStorageService;
import nl.jordaan.csprocessor.service.statement.processing.IStatementBatchProcessingService;
import nl.jordaan.csprocessor.objectmodel.dto.GetExecutionDetailsResponse;
import nl.jordaan.csprocessor.objectmodel.dto.GetExecutionStatusResponse;
import nl.jordaan.csprocessor.objectmodel.dto.UploadStatementResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

@RestController
@RequestMapping("/rest-api/v1/customer-statement/processing/validation-jobs")
public class ValidateStatementController {

    @Autowired
    private IStatementBatchProcessingService statementProcessingService;

    @Autowired
    private IFileStorageService fileStorageService;

    @PostMapping(value = "/upload", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public UploadStatementResponse uploadForValidation(@RequestParam("file") MultipartFile file) {
        try {
            Path path = fileStorageService.store(file);
            Long jobId = statementProcessingService.processStatement(path, true);
            return new UploadStatementResponse(jobId);
        } catch (final IOException ex) {
            throw new StatementProcessingApiException(ReferenceNumberGenerator.generate(), "Failed to upload statement for processing: " + ex, ex);
        }
    }

    @GetMapping(path = "/{executionId}/status", produces = MediaType.APPLICATION_JSON_VALUE)
    public GetExecutionStatusResponse getStatus(@PathVariable long executionId) {
        ExecutionStatus executionStatus = statementProcessingService.getExecutionStatus(executionId);
        if (executionStatus != null) {
            return new GetExecutionStatusResponse(executionId, executionStatus.name());
        } else {
            throw new StatementProcessingApiException(ReferenceNumberGenerator.generate(), String.format("No data found for execution id %s", executionId));
        }
    }

    @GetMapping(path = "/{executionId}/details", produces = MediaType.APPLICATION_JSON_VALUE)
    public GetExecutionDetailsResponse getDetails(@PathVariable long executionId) {
        ExecutionDetails executionDetails = statementProcessingService.getExecutionDetails(executionId);
        if (executionDetails != null) {
            return new GetExecutionDetailsResponse(executionId,
                    Optional.ofNullable(executionDetails.getStatus()).map(Enum::name).orElse(null),
                    executionDetails.getCreateTime(), executionDetails.getStartTime(), executionDetails.getEndTime(),
                    Optional.ofNullable(executionDetails.getInputFile()).map(e -> e.getFileName().toString()).orElse(null));
        } else {
            throw new StatementProcessingApiException(ReferenceNumberGenerator.generate(), String.format("No data found for execution id %s", executionId));
        }
    }

    @GetMapping(path = "/{executionId}/results")
    public ResponseEntity<Resource> downloadResults(@PathVariable long executionId) throws IOException {

        ExecutionDetails executionDetails = statementProcessingService.getExecutionDetails(executionId);

        if (executionDetails != null) {
            if (executionDetails.getStatus() == ExecutionStatus.COMPLETED) {
                if (Files.exists(executionDetails.getOutputFile())) {
                    HttpHeaders headers = new HttpHeaders();
                    headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + Optional.ofNullable(executionDetails.getOutputFile()).map(e -> e.getFileName().toString()).orElse(null));
                    headers.add(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate");
                    headers.add(HttpHeaders.PRAGMA, "no-cache");
                    headers.add(HttpHeaders.EXPIRES, "0");

                    ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(executionDetails.getOutputFile()));

                    return ResponseEntity.ok()
                            .headers(headers)
                            .contentLength(executionDetails.getOutputFile().toFile().length())
                            .contentType(MediaType.APPLICATION_OCTET_STREAM)
                            .body(resource);
                } else {
                    throw new StatementProcessingApiException(ReferenceNumberGenerator.generate(), String.format("Output file does not exist for execution id %s.", executionId));
                }
            } else {
                throw new StatementProcessingApiException(ReferenceNumberGenerator.generate(), String.format("No output file is available for execution id %s because the processing is not completed.", executionId));
            }
        } else {
            throw new StatementProcessingApiException(ReferenceNumberGenerator.generate(), String.format("No data found for execution id %s", executionId));
        }
    }

}
