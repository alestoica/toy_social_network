package org.example.validators;

import org.example.domain.Message;

import java.util.ArrayList;

public class MessageValidator implements Validator<Message> {
    @Override
    public void validate(Message message) throws ValidationException {
        String errors = "";

        if(message.getId() == null)
            errors = errors + "The ID cannot be null!\n";

        if(message.getFrom() == null)
            errors = errors + "The sending user cannot be null!\n";

        ArrayList<Long> receivers = message.getTo();
        for (Long receiver : receivers) {
            if (receiver == null) {
                errors = errors + "The receiving user cannot be null!\n";
                break;
            }
        }

        if(message.getMessage() == null)
            errors = errors + "The message cannot be null!";

        if(!errors.isEmpty())
            throw new ValidationException(errors);
    }
}
