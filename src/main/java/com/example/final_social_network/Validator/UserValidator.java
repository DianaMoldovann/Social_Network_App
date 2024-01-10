package com.example.final_social_network.Validator;
import com.example.final_social_network.Domain.User;

import java.util.function.Predicate;

public class UserValidator implements Validator<User> {
    public UserValidator() {}

    @Override
    public void validate(User entity) throws ValidationException {
        String err = "";
        Predicate<String> stringNevid = (s -> !s.isEmpty());
        Predicate<String> stringValid = stringNevid.and(s -> s.matches("^[a-zA-Z\\s]+$"));
        Predicate<String> emailValid = stringNevid.and(s -> s.endsWith("@yahoo.com") ||
                                                            s.endsWith("@gmail.com") ||
                                                            s.endsWith("@hotmail.com") ||
                                                            s.endsWith("@aol.com") ||
                                                            s.endsWith("@icloud.com") ||
                                                            s.endsWith("@linkedin.com"));
        if (!stringValid.test(entity.getFirstName())) { err += "Invalid first name! \n";}
        if (!stringValid.test(entity.getLastName())) { err += "Invalid last name invalid!\n";}
        if (!emailValid.test(entity.getEmail())) { err += "Invalid email! \n";}

        if (!err.isEmpty()) {
            throw new ValidationException(err);
        }
        }
}

