package nl.jordaan.csprocessor.test.unit.objectmodel;

import nl.jordaan.csprocessor.objectmodel.validation.validator.StatementRecordBalanceValidator;
import nl.jordaan.csprocessor.objectmodel.statement.StatementRecord;
import nl.jordaan.csprocessor.objectmodel.validation.result.ValidationOutcome;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

public class StatementRecordBalanceValidatorTestCase {

    private StatementRecordBalanceValidator validator;
    private StatementRecord statementRecord;

    @BeforeEach
    public void init() {
        validator = new StatementRecordBalanceValidator();
        statementRecord =
                new StatementRecord(999, "NL69ABNA0433647324",
                        new BigDecimal("0"), "0", new BigDecimal("0"),
                        RandomStringUtils.randomAlphanumeric(20));
    }

    @Test
    public void testIncorrectBalance() {
        statementRecord.setStartBalance(new BigDecimal("500.00"));
        statementRecord.setMutation("-50.88");
        statementRecord.setEndBalance(new BigDecimal("245.26"));
        System.out.println(statementRecord);
        ValidationOutcome validationOutcome = validator.validate(statementRecord);
        System.out.println(validationOutcome);
        Assertions.assertNotNull(validationOutcome);
        Assertions.assertNotNull(validationOutcome.getValidationFailures());
        Assertions.assertFalse(validationOutcome.getValidationFailures().isEmpty());
    }

    @Test
    public void testCorrectBalance_positive_mutation() {
        statementRecord.setStartBalance(new BigDecimal("245.00"));
        statementRecord.setMutation("20.15");
        statementRecord.setEndBalance(new BigDecimal("265.15"));
        System.out.println(statementRecord);
        ValidationOutcome validationOutcome = validator.validate(statementRecord);
        System.out.println(validationOutcome);
        Assertions.assertNotNull(validationOutcome);
        Assertions.assertNull(validationOutcome.getValidationFailures());
    }

    @Test
    public void testCorrectBalance_no_mutation() {
        statementRecord.setStartBalance(new BigDecimal("245.00"));
        statementRecord.setMutation("+0.0");
        statementRecord.setEndBalance(new BigDecimal("245.00"));
        System.out.println(statementRecord);
        ValidationOutcome validationOutcome = validator.validate(statementRecord);
        System.out.println(validationOutcome);
        Assertions.assertNotNull(validationOutcome);
        Assertions.assertNull(validationOutcome.getValidationFailures());
    }

    @Test
    public void testCorrectBalance_negative_mutation() {
        statementRecord.setStartBalance(new BigDecimal("12.00"));
        statementRecord.setMutation("-24.2");
        statementRecord.setEndBalance(new BigDecimal("-12.2"));
        System.out.println(statementRecord);
        ValidationOutcome validationOutcome = validator.validate(statementRecord);
        System.out.println(validationOutcome);
        Assertions.assertNotNull(validationOutcome);
        Assertions.assertNull(validationOutcome.getValidationFailures());
    }
}
