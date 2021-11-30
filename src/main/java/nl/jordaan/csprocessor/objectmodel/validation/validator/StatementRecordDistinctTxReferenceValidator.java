package nl.jordaan.csprocessor.objectmodel.validation.validator;

import com.google.common.collect.Lists;
import nl.jordaan.csprocessor.objectmodel.statement.StatementRecord;
import nl.jordaan.csprocessor.objectmodel.validation.result.ValidationFailure;
import nl.jordaan.csprocessor.objectmodel.validation.result.ValidationOutcome;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class StatementRecordDistinctTxReferenceValidator implements IValidator<StatementRecord> {

    private static final String VALIDATOR_ID = "StatementRecordDistinctTxReferenceValidator";

    private final Set<Long> existingTxReferences = ConcurrentHashMap.newKeySet();

    @Override
    public String getId() {
        return VALIDATOR_ID;
    }

    @Override
    public ValidationOutcome validate(final StatementRecord item) {
        if (existingTxReferences.add(item.getTransactionReference())) {
            return new ValidationOutcome(VALIDATOR_ID, true);
        } else {
            return new ValidationOutcome(VALIDATOR_ID, false, Lists.newArrayList(new ValidationFailure("unique-transaction-reference", "Duplicate transaction reference")));
        }
    }
}
