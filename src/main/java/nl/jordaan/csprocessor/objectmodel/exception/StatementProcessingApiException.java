package nl.jordaan.csprocessor.objectmodel.exception;

import com.google.common.base.Objects;

public class StatementProcessingApiException extends RuntimeException {

    private final String referenceNumber;

    public StatementProcessingApiException(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    public StatementProcessingApiException(String referenceNumber, String message) {
        super(message);
        this.referenceNumber = referenceNumber;
    }

    public StatementProcessingApiException(String referenceNumber, String message, Throwable cause) {
        super(message, cause);
        this.referenceNumber = referenceNumber;
    }

    public StatementProcessingApiException(String referenceNumber, Throwable cause) {
        super(cause);
        this.referenceNumber = referenceNumber;
    }

    public StatementProcessingApiException(String referenceNumber, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.referenceNumber = referenceNumber;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StatementProcessingApiException that = (StatementProcessingApiException) o;
        return super.equals(o) && Objects.equal(referenceNumber, that.referenceNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getMessage(), referenceNumber);
    }
}
