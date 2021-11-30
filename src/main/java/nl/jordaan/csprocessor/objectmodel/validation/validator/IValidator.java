package nl.jordaan.csprocessor.objectmodel.validation.validator;

import nl.jordaan.csprocessor.objectmodel.validation.result.ValidationOutcome;

public interface IValidator<T> {
    String getId();
    ValidationOutcome validate(T item);
}
