package nl.jordaan.csprocessor.objectmodel.validation.result;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import javax.xml.bind.annotation.*;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType( propOrder = {"transactionReference", "accountNumber", "valid", "validationErrors"})
@XmlRootElement(name = "result")
public class StatementRecordValidationSummary {

    @XmlElement(name = "transactionReference", required = true)
    private long transactionReference;
    @XmlElement(name = "accountNumber", required = true)
    private String accountNumber;
    @XmlElement(required = true)
    private boolean valid;
    @XmlElementWrapper(name="failures")
    @XmlElement(name="failure")
    private List<String> validationErrors;

    public StatementRecordValidationSummary() {
    }

    public StatementRecordValidationSummary(long transactionReference, String accountNumber, boolean valid) {
        this.transactionReference = transactionReference;
        this.accountNumber = accountNumber;
        this.valid = valid;
    }

    public StatementRecordValidationSummary(long transactionReference, String accountNumber, boolean valid, List<String> validationErrors) {
        this.transactionReference = transactionReference;
        this.accountNumber = accountNumber;
        this.valid = valid;
        this.validationErrors = validationErrors;
    }

    public long getTransactionReference() {
        return transactionReference;
    }

    public void setTransactionReference(long transactionReference) {
        this.transactionReference = transactionReference;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public List<String> getValidationErrors() {
        return validationErrors;
    }

    public void setValidationErrors(List<String> validationErrors) {
        this.validationErrors = validationErrors;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StatementRecordValidationSummary that = (StatementRecordValidationSummary) o;
        return transactionReference == that.transactionReference &&
                valid == that.valid &&
                Objects.equal(accountNumber, that.accountNumber) &&
                Objects.equal(validationErrors, that.validationErrors);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(transactionReference, accountNumber);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("transactionReference", transactionReference)
                .add("accountNumber", accountNumber)
                .add("valid", valid)
                .add("validationErrors", validationErrors)
                .toString();
    }
}
