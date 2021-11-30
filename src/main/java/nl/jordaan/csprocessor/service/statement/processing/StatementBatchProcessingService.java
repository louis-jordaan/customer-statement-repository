package nl.jordaan.csprocessor.service.statement.processing;

import nl.jordaan.csprocessor.objectmodel.ReferenceNumberGenerator;
import nl.jordaan.csprocessor.objectmodel.constant.BatchJobParameter;
import nl.jordaan.csprocessor.objectmodel.constant.ExecutionStatus;
import nl.jordaan.csprocessor.objectmodel.exception.StatementProcessingApiException;
import nl.jordaan.csprocessor.objectmodel.execution.ExecutionDetails;
import nl.jordaan.csprocessor.objectmodel.DateTimeUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.*;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
public class StatementBatchProcessingService implements IStatementBatchProcessingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(StatementBatchProcessingService.class);

    private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyy_MM_dd__HH_mm_ss_SSS");

    @Autowired
    @Qualifier("asyncJobLauncher")
    private JobLauncher asyncJobLauncher;

    @Autowired
    @Qualifier("jobLauncher")
    private JobLauncher jobLauncher;

    @Autowired
    @Qualifier("validateCsvFileJob")
    private Job validateCsvJob;

    @Autowired
    @Qualifier("validateXmlFileJob")
    private Job validateXmlJob;

    @Autowired
    private JobExplorer jobExplorer;

    @Override
    public Long processStatement(Path inputFile, boolean async) {
        return processStatement(inputFile, generateDefaultOutputFile(inputFile), async);
    }

    @Override
    public Long processStatement(Path inputFile, Path outputFile, boolean async) {
        try {
            JobExecution jobExecution = getJobLauncher(async).run(determineJobByFileName(inputFile.getFileName().toString()), createJobParameters(inputFile, outputFile));
            LOGGER.info("Job {} launched at {} with parameters {}", jobExecution.getJobId(), jobExecution.getCreateTime(), jobExecution.getJobParameters());
            return jobExecution.getJobId();
        } catch (JobExecutionAlreadyRunningException ex) {
            throw new StatementProcessingApiException(ReferenceNumberGenerator.generate(), "Processing job is already running: " + ex, ex);
        } catch (JobRestartException ex) {
            throw new StatementProcessingApiException(ReferenceNumberGenerator.generate(), "Failed to restart job: " + ex, ex);
        } catch (JobInstanceAlreadyCompleteException ex) {
            throw new StatementProcessingApiException(ReferenceNumberGenerator.generate(), "Processing job execution is already complete: " + ex, ex);
        } catch (JobParametersInvalidException ex) {
            throw new StatementProcessingApiException(ReferenceNumberGenerator.generate(), "Invalid processing job parameters: " + ex, ex);
        }
    }

    @Override
    public ExecutionStatus getExecutionStatus(long executionId) {
        JobExecution jobExecution = jobExplorer.getJobExecution(executionId);
        if (jobExecution != null) {
            return getExecutionStatus(jobExecution.getStatus());
        }
        return null;
    }

    @Override
    public ExecutionDetails getExecutionDetails(long executionId) {
        JobExecution jobExecution = jobExplorer.getJobExecution(executionId);
        if (jobExecution != null) {
            return new ExecutionDetails.Builder(jobExecution.getJobId())
                    .status(getExecutionStatus(jobExecution.getStatus()))
                    .createTime(DateTimeUtil.convertToZonedDateTime(jobExecution.getCreateTime()))
                    .startTime(DateTimeUtil.convertToZonedDateTime(jobExecution.getStartTime()))
                    .endTime(DateTimeUtil.convertToZonedDateTime(jobExecution.getEndTime()))
                    .inputFile(Optional.ofNullable(jobExecution.getJobParameters().getString(BatchJobParameter.INPUT_FILE_NAME.getKey())).map(Path::of).orElse(null))
                    .outputFile(Optional.ofNullable(jobExecution.getJobParameters().getString(BatchJobParameter.OUTPUT_FILE_NAME.getKey())).map(Path::of).orElse(null))
                    .build();
        }
        return null;
    }

    private JobLauncher getJobLauncher(boolean async) {
        return async ? asyncJobLauncher : jobLauncher;
    }

    private Job determineJobByFileName(String fileName) {
        if (StringUtils.endsWithIgnoreCase(fileName, ".xml")) {
            return validateXmlJob;
        } else if (StringUtils.endsWithIgnoreCase(fileName, ".csv")) {
            return validateCsvJob;
        } else {
            throw new IllegalArgumentException("File type not supported.");
        }
    }

    private static JobParameters createJobParameters(final Path inputFile, final Path outputFile) {
        return new JobParametersBuilder()
                .addString(BatchJobParameter.INPUT_FILE_NAME.getKey(), inputFile.toAbsolutePath().toString())
                .addString(BatchJobParameter.OUTPUT_FILE_NAME.getKey(), outputFile.toAbsolutePath().toString())
                .toJobParameters();
    }

    private static Path generateDefaultOutputFile(final Path inputFile) {
        return Path.of(String.join("",
                StringUtils.substringBeforeLast(inputFile.toString(), "."),
                "_output_", TIMESTAMP_FORMAT.format(LocalDateTime.now()), ".",
                StringUtils.substringAfterLast(inputFile.getFileName().toString(), ".")));
    }

    private static ExecutionStatus getExecutionStatus(BatchStatus batchStatus) {
        if (batchStatus != null) {
            switch (batchStatus) {
                case COMPLETED:
                    return ExecutionStatus.COMPLETED;
                case STARTING: // fall through
                case STARTED:  // fall through
                case STOPPING:
                    return ExecutionStatus.IN_PROGRESS;
                case STOPPED:
                    return ExecutionStatus.STOPPED;
                case ABANDONED:
                    return ExecutionStatus.CANCELLED;
                case FAILED:
                    return ExecutionStatus.FAILED;
                default:
                    return ExecutionStatus.UNKNOWN;
            }
        }
        return null;
    }
}
