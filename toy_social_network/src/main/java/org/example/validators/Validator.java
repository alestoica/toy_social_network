package org.example.validators;

public interface Validator<T> {
    /**
     * validates an entity to ensure it meets specific criteria
     *
     * @param entity the entity to be validated
     *
     * @throws ValidationException if the entity fails validation, a ValidationException is thrown
     *                            with details about the validation failure
     */
    void validate(T entity) throws ValidationException;
}
