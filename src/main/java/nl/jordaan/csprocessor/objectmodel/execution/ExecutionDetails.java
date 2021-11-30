package nl.jordaan.csprocessor.objectmodel.execution;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import nl.jordaan.csprocessor.objectmodel.constant.ExecutionStatus;

import java.nio.file.Path;
import java.time.ZonedDateTime;

public class ExecutionDetails {

    private final long executionId;
    private final ExecutionStatus status;
    private final ZonedDateTime createTime;
    private final ZonedDateTime startTime;
    private final ZonedDateTime endTime;
    private final Path inputFile;
    private final Path outputFile;

    private ExecutionDetails(long executionId, ExecutionStatus status, ZonedDateTime createTime, ZonedDateTime startTime,
                            ZonedDateTime endTime, Path inputFile, Path outputFile) {
        this.executionId = executionId;
        this.status = status;
        this.createTime = createTime;
        this.startTime = startTime;
        this.endTime = endTime;
        this.inputFile = inputFile;
        this.outputFile = outputFile;
    }

    public long getExecutionId() {
        return executionId;
    }

    public ExecutionStatus getStatus() {
        return status;
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

    public Path getInputFile() {
        return inputFile;
    }

    public Path getOutputFile() {
        return outputFile;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExecutionDetails that = (ExecutionDetails) o;
        return executionId == that.executionId &&
                status == that.status &&
                Objects.equal(createTime, that.createTime) &&
                Objects.equal(startTime, that.startTime) &&
                Objects.equal(endTime, that.endTime) &&
                Objects.equal(inputFile, that.inputFile) &&
                Objects.equal(outputFile, that.outputFile);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(executionId);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("executionId", executionId)
                .add("status", status)
                .add("createTime", createTime)
                .add("startTime", startTime)
                .add("endTime", endTime)
                .add("inputFile", inputFile)
                .add("outputFile", outputFile)
                .toString();
    }

    public static class Builder {

        private final long executionId;
        private ExecutionStatus status;
        private ZonedDateTime createTime;
        private ZonedDateTime startTime;
        private ZonedDateTime endTime;
        private Path inputFile;
        private Path outputFile;

        public Builder(long executionId) {
            this.executionId = executionId;
        }

        public Builder status(ExecutionStatus status) {
            this.status = status;
            return this;
        }

        public Builder createTime(ZonedDateTime createTime) {
            this.createTime = createTime;
            return this;
        }

        public Builder startTime(ZonedDateTime startTime) {
            this.startTime = startTime;
            return this;
        }

        public Builder endTime(ZonedDateTime endTime) {
            this.endTime = endTime;
            return this;
        }

        public Builder inputFile(Path inputFile) {
            this.inputFile = inputFile;
            return this;
        }

        public Builder outputFile(Path outputFile) {
            this.outputFile = outputFile;
            return this;
        }

        public ExecutionDetails build() {
            return new ExecutionDetails(this.executionId, this.status, this.createTime, this.startTime,
                    this.endTime, this.inputFile, this.outputFile);
        }
    }
}
