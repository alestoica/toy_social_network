package org.example.validators;

import org.example.domain.Request;

public class RequestValidator implements Validator<Request> {

    @Override
    public void validate(Request request) throws ValidationException {
        String errors = "";

        if(request.getId() == null)
            errors = errors + "The ID cannot be null!\n";

        if(request.getFrom() == null)
            errors = errors + "The sending user cannot be null!\n";

        if(request.getTo() == null)
            errors = errors + "The receiving user cannot be null!\n";

        if(!errors.isEmpty())
            throw new ValidationException(errors);
    }
}
