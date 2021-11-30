package nl.jordaan.csprocessor.objectmodel.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

public class GetExecutionStatusResponse {

    @JsonProperty(required = true)
    private final long executionId;
    @JsonProperty(required = true)
    private final String executionStatus;

    public GetExecutionStatusResponse(@JsonProperty("executionId") long executionId, @JsonProperty("executionStatus") String executionStatus) {
        this.executionId = executionId;
        this.executionStatus = executionStatus;
    }

    public long getExecutionId() {
        return executionId;
    }

    public String getExecutionStatus() {
        return executionStatus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GetExecutionStatusResponse that = (GetExecutionStatusResponse) o;
        return executionId == that.executionId &&
                Objects.equal(executionStatus, that.executionStatus);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(executionId);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("executionId", executionId)
                .add("executionStatus", executionStatus)
                .toString();
    }
}
