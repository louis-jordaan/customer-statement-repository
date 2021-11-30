package nl.jordaan.csprocessor.application.error;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import java.time.LocalDateTime;

public class StatementProcessorErrorResponse {

    private final LocalDateTime timestamp = LocalDateTime.now();
    private String referenceNumber;
    private int status;
    private String error;
    private String message;
    private String path;

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StatementProcessorErrorResponse that = (StatementProcessorErrorResponse) o;
        return status == that.status &&
                Objects.equal(timestamp, that.timestamp) &&
                Objects.equal(referenceNumber, that.referenceNumber) &&
                Objects.equal(error, that.error) &&
                Objects.equal(message, that.message) &&
                Objects.equal(path, that.path);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(referenceNumber);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("timestamp", timestamp)
                .add("referenceNumber", referenceNumber)
                .add("status", status)
                .add("error", error)
                .add("message", message)
                .add("path", path)
                .toString();
    }
}
