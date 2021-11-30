package nl.jordaan.csprocessor.objectmodel.validation.result;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

public class ValidationFailure {

    private String validationId;
    private String failureDetails;

    public ValidationFailure(String validationId, String failureDetails) {
        this.validationId = validationId;
        this.failureDetails = failureDetails;
    }

    public String getValidationId() {
        return validationId;
    }

    public void setValidationId(String validationId) {
        this.validationId = validationId;
    }

    public String getFailureDetails() {
        return failureDetails;
    }

    public void setFailureDetails(String failureDetails) {
        this.failureDetails = failureDetails;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ValidationFailure that = (ValidationFailure) o;
        return Objects.equal(validationId, that.validationId) &&
                Objects.equal(failureDetails, that.failureDetails);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(validationId);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("validationId", validationId)
                .add("failureDetails", failureDetails)
                .toString();
    }
}
