package nl.jordaan.csprocessor.objectmodel.validation.result;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import java.util.List;

public class ValidationOutcome {

    private String validatorId;
    private boolean valid;
    private List<ValidationFailure> validationFailures;

    public ValidationOutcome(String validatorId, boolean valid) {
        this.valid = valid;
    }

    public ValidationOutcome(String validatorId, boolean valid, List<ValidationFailure> validationFailures) {
        this.valid = valid;
        this.validationFailures = validationFailures;
    }

    public String getValidatorId() {
        return validatorId;
    }

    public void setValidatorId(String validatorId) {
        this.validatorId = validatorId;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public List<ValidationFailure> getValidationFailures() {
        return validationFailures;
    }

    public void setValidationFailures(List<ValidationFailure> validationFailures) {
        this.validationFailures = validationFailures;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ValidationOutcome that = (ValidationOutcome) o;
        return valid == that.valid &&
                Objects.equal(validationFailures, that.validationFailures);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(valid);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("validatorId", validatorId)
                .add("valid", valid)
                .add("validationFailures", validationFailures)
                .toString();
    }
}
