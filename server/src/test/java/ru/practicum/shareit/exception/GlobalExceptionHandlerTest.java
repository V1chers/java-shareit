package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.bind.MethodArgumentNotValidException;
import ru.practicum.shareit.exception.exceptions.ConditionsNotMetException;
import ru.practicum.shareit.exception.exceptions.ConflictException;
import ru.practicum.shareit.exception.exceptions.NotFoundException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler exceptionHandler;

    @Test
    void handleNotFoundException() {
        NotFoundException exception = new NotFoundException("Item not found");

        ErrorResponse response = exceptionHandler.handleNotFoundException(exception);

        assertThat(response.getError(), is("Item not found"));
    }

    @Test
    void handleValidException() {
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        when(exception.getMessage()).thenReturn("Validation failed");

        ErrorResponse response = exceptionHandler.handleValidException(exception);

        assertThat(response.getError(), is("Validation failed"));
    }

    @Test
    void handleConflictException() {
        ConflictException exception = new ConflictException("Email already exists");

        ErrorResponse response = exceptionHandler.handleConflictException(exception);

        assertThat(response.getError(), is("Email already exists"));
    }

    @Test
    void handeConditionsNoteMetException() {
        ConditionsNotMetException exception = new ConditionsNotMetException("Conditions not met");

        ErrorResponse response = exceptionHandler.handeConditionsNoteMetException(exception);

        assertThat(response.getError(), is("Conditions not met"));
    }

    @Test
    void handleException() {
        RuntimeException exception = new RuntimeException("Unexpected error");

        ErrorResponse response = exceptionHandler.handleException(exception);

        assertThat(response.getError(), is("Unexpected error"));
    }
}
