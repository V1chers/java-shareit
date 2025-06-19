package ru.practicum.shareit.validation;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.exception.exceptions.NotFoundException;

import java.util.Optional;

public class ValidationUtils {

    public static <T, K> void isExist(JpaRepository<T, K> repository, K id, String message) {
        Optional<T> object = repository.findById(id);

        if (object.isEmpty()) {
            throw new NotFoundException(message + ": " + id);
        }
    }
}
