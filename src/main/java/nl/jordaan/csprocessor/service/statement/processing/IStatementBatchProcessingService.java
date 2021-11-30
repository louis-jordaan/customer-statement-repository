package nl.jordaan.csprocessor.service.statement.processing;

import nl.jordaan.csprocessor.objectmodel.constant.ExecutionStatus;
import nl.jordaan.csprocessor.objectmodel.execution.ExecutionDetails;

import java.nio.file.Path;

public interface IStatementBatchProcessingService {

    Long processStatement(Path inputFile, boolean async);

    Long processStatement(Path inputFile, Path outputFile, boolean async);

    ExecutionStatus getExecutionStatus(long executionId);

    ExecutionDetails getExecutionDetails(long executionId);
}
