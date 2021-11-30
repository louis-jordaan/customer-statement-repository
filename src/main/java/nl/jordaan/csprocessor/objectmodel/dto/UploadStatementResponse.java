package nl.jordaan.csprocessor.objectmodel.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

public class UploadStatementResponse {

    @JsonProperty(required = true)
    private final long executionId;

    public UploadStatementResponse(long executionId) {
        this.executionId = executionId;
    }

    public long getExecutionId() {
        return executionId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UploadStatementResponse that = (UploadStatementResponse) o;
        return executionId == that.executionId;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(executionId);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("executionId", executionId)
                .toString();
    }
}
