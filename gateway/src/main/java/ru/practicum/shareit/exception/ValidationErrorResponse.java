package ru.practicum.shareit.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.validation.FieldError;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class ValidationErrorResponse extends ErrorResponse {
    List<FieldErrorDto> fieldErrors;

    public ValidationErrorResponse(String error, List<FieldError> fieldErrors) {
        super(error);

        this.fieldErrors = fieldErrors.stream()
                .map(fieldError -> new FieldErrorDto(fieldError.getField(), fieldError.getDefaultMessage()))
                .toList();
    }
}
