package nl.jordaan.csprocessor.objectmodel.validation.validator;

import com.google.common.collect.Lists;
import nl.jordaan.csprocessor.objectmodel.statement.StatementRecord;
import nl.jordaan.csprocessor.objectmodel.validation.result.ValidationFailure;
import nl.jordaan.csprocessor.objectmodel.validation.result.ValidationOutcome;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class StatementRecordRequiredFieldsValidator implements IValidator<StatementRecord> {

    private static final String VALIDATOR_ID = "StatementRecordRequiredFieldsValidator";

    @Override
    public String getId() {
        return VALIDATOR_ID;
    }

    @Override
    public ValidationOutcome validate(final StatementRecord statementRecord) {

        List<ValidationFailure> validationFailures = Lists.newArrayList();

        if (StringUtils.isBlank(statementRecord.getAccountNumber())) {
            validationFailures.add(new ValidationFailure("account-number-provided", "Account number is missing"));
        }
        if (statementRecord.getStartBalance() == null) {
            validationFailures.add(new ValidationFailure("start-balance-provided", "Start balance is missing"));
        }
        if (StringUtils.isBlank(statementRecord.getMutation())) {
            validationFailures.add(new ValidationFailure("mutation-amount-provided", "Transaction mutation is missing"));
        }
        if (statementRecord.getEndBalance() == null) {
            validationFailures.add(new ValidationFailure("end-balance-provided", "End balance is missing"));
        }

        return validationFailures.isEmpty()
                ? new ValidationOutcome(VALIDATOR_ID, true)
                : new ValidationOutcome(VALIDATOR_ID, false, validationFailures);
    }
}
