package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.enums.State;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

public class BookingMapper {
    public static BookingDto toDto(Booking booking) {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(booking.getId());
        bookingDto.setStart(toLocalDateTime(booking.getStart()));
        bookingDto.setEnd(toLocalDateTime(booking.getEnd()));
        bookingDto.setItem(booking.getItem());
        bookingDto.setBooker(booking.getBooker());

        fillStatus(bookingDto, booking.getApproved());

        return bookingDto;
    }

    public static List<BookingDto> toDto(List<Booking> bookings) {
        return bookings.stream().map(BookingMapper::toDto).toList();
    }

    public static Booking fromDto(BookingDto bookingDto) {
        Booking booking = new Booking();
        booking.setId(bookingDto.getId());
        booking.setStart(toInstant(bookingDto.getStart()));
        booking.setEnd(toInstant(bookingDto.getEnd()));
        booking.setItem(bookingDto.getItem());
        booking.setBooker(bookingDto.getBooker());

        return booking;
    }

    public static Booking fromCreateDto(CreateBookingDto bookingDto, int userId) {
        Booking booking = new Booking();
        Item item = new Item();
        User user = new User();

        item.setId(bookingDto.getItemId());
        user.setId(userId);

        booking.setStart(toInstant(bookingDto.getStart()));
        booking.setEnd(toInstant(bookingDto.getEnd()));
        booking.setItem(item);
        booking.setBooker(user);

        return booking;
    }

    private static void fillStatus(BookingDto bookingDto, Boolean approved) {
        if (approved == null && bookingDto.getStart().isBefore(LocalDateTime.now())) {
            bookingDto.setStatus(State.REJECTED);
            return;
        } else if (approved == null && bookingDto.getStart().isAfter(LocalDateTime.now())) {
            bookingDto.setStatus(State.WAITING);
            return;
        }

        if (!approved) {
            bookingDto.setStatus(State.REJECTED);
        } else if (bookingDto.getEnd().isAfter(LocalDateTime.now())) {
            bookingDto.setStatus(State.PAST);
        } else if (bookingDto.getEnd().isBefore(LocalDateTime.now()) && bookingDto.getStart().isAfter(LocalDateTime.now())) {
            bookingDto.setStatus(State.CURRENT);
        } else if (bookingDto.getStart().isAfter(LocalDateTime.now())) {
            bookingDto.setStatus(State.FUTURE);
        }
    }

    private static Instant toInstant(LocalDateTime localDateTime) {
        return localDateTime.atZone(ZoneId.of("UTC+3")).toInstant();
    }

    private static LocalDateTime toLocalDateTime(Instant instant) {
        return instant.atZone(ZoneId.of("UTC+3")).toLocalDateTime();
    }
}
