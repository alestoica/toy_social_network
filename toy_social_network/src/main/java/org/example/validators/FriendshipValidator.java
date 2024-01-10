package org.example.validators;

import org.example.domain.Friendship;

public class FriendshipValidator implements Validator<Friendship> {
    @Override
    public void validate(Friendship entity) throws ValidationException {
        String errors = "";

        if(entity.getId().getRight() == null || entity.getId().getLeft() == null)
            errors = errors + "The IDs cannot be null!\n";

        if(entity.getId().getRight().equals(entity.getId().getLeft()))
            errors = errors + "An user cannot befriend himself!\n";

        if(!errors.isEmpty())
            throw new ValidationException(errors);
    }
}
