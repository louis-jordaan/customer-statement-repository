package nl.jordaan.csprocessor.service.statement.validation;

import nl.jordaan.csprocessor.objectmodel.statement.StatementRecord;
import nl.jordaan.csprocessor.objectmodel.validation.result.ValidationOutcome;

public interface IStatementValidationService {
    ValidationOutcome validateStatementRecord(StatementRecord statementRecord);
}
