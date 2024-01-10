package com.example.final_social_network.Validator;

import com.example.final_social_network.Domain.Message;

public class MessageValidator implements Validator<Message> {
    @Override
    public void validate(Message entity) throws ValidationException {
        String err = "";
        if (entity.getMessage().isEmpty()) { err += "You can not send a empty message! \n";}
        if (entity.getTo().isEmpty()) { err += "Please select one or more people to send a message to! ";}

        if (!err.isEmpty()) {
            throw new ValidationException(err);
        }
    }
}
