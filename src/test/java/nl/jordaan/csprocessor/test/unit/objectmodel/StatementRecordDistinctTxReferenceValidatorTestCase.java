package nl.jordaan.csprocessor.test.unit.objectmodel;

import com.google.common.collect.Lists;
import nl.jordaan.csprocessor.objectmodel.statement.StatementRecord;
import nl.jordaan.csprocessor.objectmodel.validation.result.ValidationOutcome;
import nl.jordaan.csprocessor.objectmodel.validation.validator.StatementRecordDistinctTxReferenceValidator;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class StatementRecordDistinctTxReferenceValidatorTestCase {

    private StatementRecordDistinctTxReferenceValidator validator;
    private List<StatementRecord> statementRecordList;

    @BeforeEach
    public void init() {
        validator = new StatementRecordDistinctTxReferenceValidator();
        statementRecordList = Lists.newArrayList();
        for (int i = 0; i < 100; i++) {
            statementRecordList.add(createStatementRecord(i, "NL69ABNA0433647324",
                    new BigDecimal(RandomStringUtils.randomNumeric(4)), String.valueOf(-50 + i),
                    new BigDecimal(RandomStringUtils.randomNumeric(4)), RandomStringUtils.randomAlphanumeric(20)));
        }
    }

    @Test
    public void test_noDuplicates() {
        boolean allValid = statementRecordList.stream().allMatch(e -> {
            ValidationOutcome validationOutcome = validator.validate(e);
            return validationOutcome != null && validationOutcome.getValidationFailures() == null;
        });
        Assertions.assertTrue(allValid);
    }

    @Test
    public void test_duplicates() {
        statementRecordList.add(statementRecordList.get(0));
        statementRecordList.add(statementRecordList.get(50));
        AtomicInteger duplicateCounter = new AtomicInteger(0);
        AtomicInteger uniqueCounter = new AtomicInteger(0);
        statementRecordList.stream().forEach(e -> {
            ValidationOutcome validationOutcome = validator.validate(e);
            if (validationOutcome != null && validationOutcome.getValidationFailures() != null && !validationOutcome.getValidationFailures().isEmpty()) {
                duplicateCounter.incrementAndGet();
            } else {
                uniqueCounter.incrementAndGet();
            }
        });
        Assertions.assertEquals(2, duplicateCounter.get());
        Assertions.assertEquals(100, uniqueCounter.get());
    }

    private StatementRecord createStatementRecord(long txRef, String accNo, BigDecimal startBalance, String mutation, BigDecimal endBalance, String description) {
        return new StatementRecord(txRef, accNo, startBalance, mutation, endBalance, description);
    }
}
