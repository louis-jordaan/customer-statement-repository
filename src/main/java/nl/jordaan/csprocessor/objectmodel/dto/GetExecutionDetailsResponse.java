package nl.jordaan.csprocessor.objectmodel.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import java.time.ZonedDateTime;

public class GetExecutionDetailsResponse {

    @JsonProperty(required = true)
    private final long executionId;
    @JsonProperty(required = true)
    private final String executionStatus;
    @JsonProperty(required = true)
    private final ZonedDateTime createTime;
    @JsonProperty
    private final ZonedDateTime startTime;
    @JsonProperty
    private final ZonedDateTime endTime;
    @JsonProperty(required = true)
    private final String inputFileName;

    public GetExecutionDetailsResponse(@JsonProperty("executionId") long executionId,
                                       @JsonProperty("executionStatus") String executionStatus,
                                       @JsonProperty("createTime") ZonedDateTime createTime,
                                       @JsonProperty("startTime") ZonedDateTime startTime,
                                       @JsonProperty("endTime") ZonedDateTime endTime,
                                       @JsonProperty("inputFileName") String inputFileName) {
        this.executionId = executionId;
        this.executionStatus = executionStatus;
        this.createTime = createTime;
        this.startTime = startTime;
        this.endTime = endTime;
        this.inputFileName = inputFileName;
    }

    public long getExecutionId() {
        return executionId;
    }

    public String getExecutionStatus() {
        return executionStatus;
    }

    public ZonedDateTime getCreateTime() {
        return createTime;
    }

    public ZonedDateTime getStartTime() {
        return startTime;
    }

    public ZonedDateTime getEndTime() {
        return endTime;
    }

    public String getInputFileName() {
        return inputFileName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GetExecutionDetailsResponse that = (GetExecutionDetailsResponse) o;
        return executionId == that.executionId &&
                Objects.equal(executionStatus, that.executionStatus) &&
                Objects.equal(createTime, that.createTime) &&
                Objects.equal(startTime, that.startTime) &&
                Objects.equal(endTime, that.endTime) &&
                Objects.equal(inputFileName, that.inputFileName);
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
                .add("createTime", createTime)
                .add("startTime", startTime)
                .add("endTime", endTime)
                .add("inputFileName", inputFileName)
                .toString();
    }
}
