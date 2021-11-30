package nl.jordaan.csprocessor.objectmodel.validation.validator;

import com.google.common.collect.Lists;
import nl.jordaan.csprocessor.objectmodel.validation.result.ValidationFailure;
import nl.jordaan.csprocessor.objectmodel.statement.StatementRecord;
import nl.jordaan.csprocessor.objectmodel.validation.result.ValidationOutcome;

import java.math.BigDecimal;

public class StatementRecordBalanceValidator implements IValidator<StatementRecord> {

    private static final String VALIDATOR_ID = "StatementRecordBalanceValidator";

    @Override
    public String getId() {
        return VALIDATOR_ID;
    }

    @Override
    public ValidationOutcome validate(final StatementRecord statementRecord) {
        BigDecimal expectedBalance = statementRecord.getStartBalance().add(new BigDecimal(statementRecord.getMutation()));
        return expectedBalance.compareTo(statementRecord.getEndBalance()) == 0
                ? new ValidationOutcome(VALIDATOR_ID, true)
                : new ValidationOutcome(VALIDATOR_ID, false, Lists.newArrayList(new ValidationFailure("transaction-end-balance", String.format("End balance is not correct. End balance should be %s", expectedBalance.toPlainString()))));
    }
}
