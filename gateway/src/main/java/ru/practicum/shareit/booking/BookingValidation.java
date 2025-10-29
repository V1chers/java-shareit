package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.exception.exceptions.ConditionsNotMetException;

@Component
@Slf4j
public class BookingValidation {
    public void isEndAfterStart(BookItemRequestDto bookingDto) {
        if (bookingDto.getEnd().isBefore(bookingDto.getStart())) {
            log.warn("Конец бронирования должен быть после его начала: start {}, end {}",
                    bookingDto.getStart(), bookingDto.getEnd());
            throw new ConditionsNotMetException("Конец бронирования должен быть после его начала");
        }
    }
}
