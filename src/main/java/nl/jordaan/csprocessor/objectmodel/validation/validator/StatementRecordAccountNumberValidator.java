package nl.jordaan.csprocessor.objectmodel.validation.validator;

import com.google.common.collect.Lists;
import nl.jordaan.csprocessor.objectmodel.validation.result.ValidationFailure;
import nl.jordaan.csprocessor.objectmodel.statement.StatementRecord;
import nl.jordaan.csprocessor.objectmodel.validation.result.ValidationOutcome;
import org.apache.commons.validator.routines.IBANValidator;

public class StatementRecordAccountNumberValidator implements IValidator<StatementRecord> {

    private static final String VALIDATOR_ID = "StatementRecordAccountNumberValidator";

    @Override
    public String getId() {
        return VALIDATOR_ID;
    }

    @Override
    public ValidationOutcome validate(final StatementRecord statementRecord) {
        return IBANValidator.DEFAULT_IBAN_VALIDATOR.isValid(statementRecord.getAccountNumber())
                ? new ValidationOutcome(VALIDATOR_ID, true)
                : new ValidationOutcome(VALIDATOR_ID, false, Lists.newArrayList(new ValidationFailure("IBAN-check", "Account number is not a valid IBAN")));
    }
}
