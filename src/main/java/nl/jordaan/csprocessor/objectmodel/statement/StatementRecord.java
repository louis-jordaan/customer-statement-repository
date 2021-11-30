package nl.jordaan.csprocessor.objectmodel.statement;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import javax.xml.bind.annotation.*;
import java.math.BigDecimal;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"accountNumber", "description", "startBalance", "mutation", "endBalance"})
@XmlRootElement(name = "record")
public class StatementRecord {

    @XmlAttribute(name = "reference", required = true)
    private long transactionReference;
    @XmlElement(required = true)
    private String accountNumber;
    @XmlElement(required = true)
    private BigDecimal startBalance;
    @XmlElement(required = true)
    private String mutation;
    @XmlElement(required = true)
    private BigDecimal endBalance;
    @XmlElement
    private String description;

    public StatementRecord() {
    }

    public StatementRecord(long transactionReference, String accountNumber, BigDecimal startBalance, String mutation, BigDecimal endBalance) {
        this.transactionReference = transactionReference;
        this.accountNumber = accountNumber;
        this.startBalance = startBalance;
        this.mutation = mutation;
        this.endBalance = endBalance;
    }

    public StatementRecord(long transactionReference, String accountNumber, BigDecimal startBalance, String mutation, BigDecimal endBalance, String description) {
        this.transactionReference = transactionReference;
        this.accountNumber = accountNumber;
        this.startBalance = startBalance;
        this.mutation = mutation;
        this.endBalance = endBalance;
        this.description = description;
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

    public BigDecimal getStartBalance() {
        return startBalance;
    }

    public void setStartBalance(BigDecimal startBalance) {
        this.startBalance = startBalance;
    }

    public String getMutation() {
        return mutation;
    }

    public void setMutation(String mutation) {
        this.mutation = mutation;
    }

    public BigDecimal getEndBalance() {
        return endBalance;
    }

    public void setEndBalance(BigDecimal endBalance) {
        this.endBalance = endBalance;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StatementRecord that = (StatementRecord) o;
        return transactionReference == that.transactionReference &&
                Objects.equal(accountNumber, that.accountNumber) &&
                Objects.equal(startBalance, that.startBalance) &&
                Objects.equal(mutation, that.mutation) &&
                Objects.equal(endBalance, that.endBalance) &&
                Objects.equal(description, that.description);
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
                .add("startBalance", startBalance)
                .add("mutation", mutation)
                .add("endBalance", endBalance)
                .add("description", description)
                .toString();
    }
}
