package org.example.validators;

import org.example.domain.User;

public class UserValidator implements Validator<User> {
    @Override
    public void validate(User user) throws ValidationException {
        String errors = "";

        if(user.getId() == null)
            errors = errors + "The ID cannot be null!\n";

        String firstName = user.getFirstName();
        if(firstName.isEmpty()){
            errors = errors + "The name cannot be null!\n";
        }
        for(int i=0; i < firstName.length(); i++){
            if(!Character.isLetter(firstName.charAt(i))){
                errors = errors + "Invalid name!\n";
                break;
            }
        }

        String lastName = user.getLastName();
        if(lastName.isEmpty()){
            errors = errors + "the surname cannot be null!\n";
        }
        for(int i=0; i<lastName.length(); i++){
            if(!Character.isLetter(lastName.charAt(i))){
                errors = errors + "Invalid surname!\n";
                break;
            }
        }

        if(!errors.isEmpty())
            throw new ValidationException(errors);
    }
}
