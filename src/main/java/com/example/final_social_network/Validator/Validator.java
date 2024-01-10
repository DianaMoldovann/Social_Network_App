package com.example.final_social_network.Validator;

public interface Validator<T> {
    void validate(T entity) throws ValidationException;
}