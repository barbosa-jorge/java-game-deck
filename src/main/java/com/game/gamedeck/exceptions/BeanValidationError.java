package com.game.gamedeck.exceptions;

import lombok.Data;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

@Data
public class BeanValidationError {
    private String field;
    private String errorMessage;

    public BeanValidationError(ObjectError objectError) {
        this.field = ((FieldError) objectError).getField();
        this.errorMessage = objectError.getDefaultMessage();
    }
}
