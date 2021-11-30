package nl.jordaan.csprocessor.test.unit.objectmodel;

import nl.jordaan.csprocessor.objectmodel.statement.StatementRecord;
import nl.jordaan.csprocessor.objectmodel.validation.result.ValidationOutcome;
import nl.jordaan.csprocessor.objectmodel.validation.validator.StatementRecordAccountNumberValidator;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

public class StatementRecordAccountNumberValidatorTestCase {

    private StatementRecordAccountNumberValidator validator;
    private StatementRecord statementRecord;

    @BeforeEach
    public void init() {
        validator = new StatementRecordAccountNumberValidator();
        statementRecord =
                new StatementRecord(999, "NL69ABNA0433647324",
                        new BigDecimal("0"), "0", new BigDecimal("0"),
                        RandomStringUtils.randomAlphanumeric(20));
    }


    @Test
    public void testValidate_success() {
        statementRecord.setAccountNumber("NL46ABNA0625805417");
        ValidationOutcome validationOutcome = validator.validate(statementRecord);
        System.out.println(validationOutcome);
        Assertions.assertNotNull(validationOutcome);
        Assertions.assertNull(validationOutcome.getValidationFailures());
    }

    @Test
    public void testValidate_fail() {
        statementRecord.setAccountNumber("NL");
        System.out.println(statementRecord);
        ValidationOutcome validationOutcome = validator.validate(statementRecord);
        System.out.println(validationOutcome);
        Assertions.assertNotNull(validationOutcome);
        Assertions.assertNotNull(validationOutcome.getValidationFailures());
        Assertions.assertFalse(validationOutcome.getValidationFailures().isEmpty());
    }
}
