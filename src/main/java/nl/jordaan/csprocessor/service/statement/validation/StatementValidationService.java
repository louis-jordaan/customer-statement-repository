package nl.jordaan.csprocessor.service.statement.validation;

import com.google.common.collect.Lists;
import nl.jordaan.csprocessor.objectmodel.validation.validator.IValidator;
import nl.jordaan.csprocessor.objectmodel.validation.validator.StatementRecordBalanceValidator;
import nl.jordaan.csprocessor.objectmodel.statement.StatementRecord;
import nl.jordaan.csprocessor.objectmodel.validation.validator.StatementRecordAccountNumberValidator;
import nl.jordaan.csprocessor.objectmodel.validation.validator.StatementRecordRequiredFieldsValidator;
import nl.jordaan.csprocessor.objectmodel.validation.result.ValidationOutcome;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional
public class StatementValidationService implements IStatementValidationService {

    private static final IValidator<StatementRecord> REQUIRED_FIELDS_VALIDATOR = new StatementRecordRequiredFieldsValidator();
    private static final List<IValidator<StatementRecord>> DEFAULT_VALIDATORS = Lists.newArrayList(new StatementRecordAccountNumberValidator(), new StatementRecordBalanceValidator());

    @Override
    public ValidationOutcome validateStatementRecord(final StatementRecord statementRecord) {

        List<ValidationOutcome> validationOutcomes = Lists.newArrayList(REQUIRED_FIELDS_VALIDATOR.validate(statementRecord));

        if (validationOutcomes.get(0).isValid()) {
            validationOutcomes.addAll(DEFAULT_VALIDATORS.stream().map(v -> v.validate(statementRecord)).collect(Collectors.toList()));
        }

        return new ValidationOutcome(this.getClass().getSimpleName(),
                validationOutcomes.stream().allMatch(ValidationOutcome::isValid),
                validationOutcomes.stream()
                        .map(ValidationOutcome::getValidationFailures)
                        .filter(Objects::nonNull)
                        .flatMap(Collection::stream)
                        .collect(Collectors.toList()));
    }
}
