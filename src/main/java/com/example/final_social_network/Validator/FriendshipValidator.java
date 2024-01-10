package com.example.final_social_network.Validator;

import com.example.final_social_network.Domain.Friendship;

import java.util.function.Predicate;

public class FriendshipValidator implements Validator<Friendship> {
    @Override
    public void validate(Friendship entity) throws ValidationException {
        String err = "";
        Predicate<Friendship> idValid = u -> (u.getId().getLeft() > 0) && (u.getId().getRight() > 0);

        if (!idValid.test(entity)) { err += "Invalid ID! \n";}

        if (!err.isEmpty()) {
            throw new ValidationException(err);
        }
    }
}
