package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.exception.exceptions.ConditionsNotMetException;


@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @GetMapping
    public ResponseEntity<Object> getBookings(@RequestHeader("X-Sharer-User-Id") long userId,
                                              @RequestParam(name = "state", defaultValue = "all") String stateParam) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new ConditionsNotMetException("Передано неизвестное состояние: " + stateParam));
        log.info("Get booking with state {}, userId={}", stateParam, userId);
        return bookingClient.getBookings(userId, state);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getOwnBookings(@RequestHeader("X-Sharer-User-Id") long userId,
                                                 @RequestParam(name = "state", defaultValue = "all") String stateParam) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new ConditionsNotMetException("Передано неизвестное состояние: " + stateParam));
        log.info("Get own booking with state {}, userId={}", stateParam, userId);
        return bookingClient.getOwnBookings(userId, state);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approveBooking(@PathVariable int bookingId,
                                                 @NotNull @RequestParam Boolean approved,
                                                 @RequestHeader("X-Sharer-User-Id") long userId) {
        return bookingClient.approveBooking(bookingId, approved, userId);
    }

    @PostMapping
    public ResponseEntity<Object> bookItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                           @RequestBody @Valid BookItemRequestDto requestDto) {
        log.info("Creating booking {}, userId={}", requestDto, userId);
        return bookingClient.bookItem(userId, requestDto);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @PathVariable Long bookingId) {
        log.info("Get booking {}, userId={}", bookingId, userId);
        return bookingClient.getBooking(userId, bookingId);
    }
}
