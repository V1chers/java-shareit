package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.enums.State;

import java.util.List;

public interface BookingService {

    BookingDto createBooking(CreateBookingDto booking, int userId);

    BookingDto approveBooking(int bookingId, Boolean approved, int userId);

    BookingDto getBooking(int bookingId, int userId);

    List<BookingDto> getBookings(State state, int userId);

    List<BookingDto> getOwnBookings(State state, int userId);
}
