package nl.jordaan.csprocessor.service.statement.processing;

import com.google.common.collect.Lists;
import nl.jordaan.csprocessor.objectmodel.validation.result.StatementRecordValidationSummary;
import nl.jordaan.csprocessor.objectmodel.validation.result.ValidationFailure;
import nl.jordaan.csprocessor.objectmodel.validation.validator.IValidator;
import nl.jordaan.csprocessor.service.statement.validation.IStatementValidationService;
import nl.jordaan.csprocessor.objectmodel.statement.StatementRecord;
import nl.jordaan.csprocessor.objectmodel.validation.result.ValidationOutcome;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/***
 * NB: This item processor stores state. New instance should be created for every job.
 */
public class StatementRecordProcessor implements ItemProcessor<StatementRecord, StatementRecordValidationSummary> {

    private static final Logger LOGGER = LoggerFactory.getLogger(StatementRecordProcessor.class);

    @Autowired
    private IStatementValidationService statementValidationService;

    private final List<IValidator<StatementRecord>> aggregateValidators;

    public StatementRecordProcessor(List<IValidator<StatementRecord>> aggregateValidators) {
        this.aggregateValidators = aggregateValidators;
    }

    @Override
    public StatementRecordValidationSummary process(final StatementRecord transactionRecord) {

        LOGGER.debug("Processing statement record: {}", transactionRecord);

        List<ValidationOutcome> validationOutcomes = Lists.newArrayList();

        if (aggregateValidators != null) {
            validationOutcomes.addAll(aggregateValidators.stream().map(v -> v.validate(transactionRecord)).collect(Collectors.toList()));
        }

        validationOutcomes.add(statementValidationService.validateStatementRecord(transactionRecord));

        LOGGER.debug("Validation outcomes: {}", Arrays.toString(validationOutcomes.toArray()));

        StatementRecordValidationSummary validationSummary =
                new StatementRecordValidationSummary(transactionRecord.getTransactionReference(),
                        transactionRecord.getAccountNumber(),
                        validationOutcomes.stream().allMatch(ValidationOutcome::isValid),
                        validationOutcomes.stream()
                                .map(ValidationOutcome::getValidationFailures)
                                .filter(Objects::nonNull)
                                .flatMap(Collection::stream)
                                .map(ValidationFailure::getFailureDetails)
                                .collect(Collectors.toList()));

        LOGGER.debug("Validation summary: {}", validationSummary);

        return validationSummary;
    }
}
