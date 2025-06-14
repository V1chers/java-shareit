package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dal.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.enums.State;
import ru.practicum.shareit.exception.exceptions.ConditionsNotMetException;
import ru.practicum.shareit.exception.exceptions.NotFoundException;
import ru.practicum.shareit.item.dal.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dal.UserRepository;
import ru.practicum.shareit.validation.ValidationService;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImp1 implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    public BookingDto createBooking(CreateBookingDto bookingDto, int userId) {
        log.info("Начинается создание аренды на вещь: запись {}, пользователь {}", bookingDto, userId);
        ValidationService.isExist(userRepository, userId, "Данный пользователь не найден");
        isItemAvailable(bookingDto.getItemId());

        Booking booking = BookingMapper.fromCreateDto(bookingDto, userId);
        booking = bookingRepository.save(booking);
        // Второе место, где такая же проблема возникает
        booking.setBooker(userRepository.findById(booking.getBooker().getId()).get());
        booking.setItem(itemRepository.findById(booking.getItem().getId()).get());

        log.info("Создание аренды прошло успешно: {},", booking);
        return BookingMapper.toDto(booking);
    }

    public BookingDto approveBooking(int bookingId, Boolean approved, int userId) {
        log.info("Начинается изменение статуса записи: запись {}, {}, пользователь {}", bookingId, approved, userId);
        if (approved == null) {
            log.warn("Передан пустой параметр approved");
            throw new ConditionsNotMetException("Передан пустой параметр approved");
        }
        isOwner(bookingId, userId);

        bookingRepository.approveBooking(approved, bookingId);

        Booking booking = findBookingById(bookingId);
        BookingDto bookingDto = BookingMapper.toDto(booking);
        bookingDto.setStatus(approved ? State.APPROVED : State.REJECTED);

        log.info("Изменение статуса записи прошло успешно: {}", booking);
        return bookingDto;
    }

    public BookingDto getBooking(int bookingId, int userId) {
        Booking booking = findBookingById(bookingId);

        isOwnerOrBooker(booking, userId, booking.getBooker().getId());

        return BookingMapper.toDto(booking);
    }

    public List<BookingDto> getBookings(State state, int userId) {
        ValidationService.isExist(userRepository, userId, "Данный пользователь не найден");

        List<Booking> bookings = bookingRepository.findAllByBookerId(userId);

        List<BookingDto> bookingDtoList = BookingMapper.toDto(bookings);
        return filterByState(state, bookingDtoList);
    }

    public List<BookingDto> getOwnBookings(State state, int userId) {
        ValidationService.isExist(userRepository, userId, "Данный пользователь не найден");

        List<Booking> bookings = bookingRepository.findAllByItemOwnerId(userId);

        List<BookingDto> bookingDtoList = BookingMapper.toDto(bookings);
        return filterByState(state, bookingDtoList);
    }

    private List<BookingDto> filterByState(State state, List<BookingDto> bookings) {
        switch (state) {
            case ALL -> {
                return bookings;
            }
            case REJECTED -> {
                return bookings.stream()
                        .filter(booking -> booking.getStatus() == State.REJECTED)
                        .toList();
            }
            case WAITING -> {
                return bookings.stream()
                        .filter(booking -> booking.getStatus() == State.WAITING)
                        .toList();
            }
            case PAST -> {
                return bookings.stream()
                        .filter(booking -> booking.getStatus() == State.PAST)
                        .toList();
            }
            case CURRENT -> {
                return bookings.stream()
                        .filter(booking -> booking.getStatus() == State.CURRENT)
                        .toList();
            }
            case FUTURE -> {
                return bookings.stream()
                        .filter(booking -> booking.getStatus() == State.FUTURE)
                        .toList();
            }
            default -> throw new ConditionsNotMetException("Передан неверный параметр state");
        }
    }

    private Booking findBookingById(int bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Данная запись не найдена"));
    }

    private void isOwner(int bookingId, int ownerId) {
        Booking booking = findBookingById(bookingId);

        if (booking.getItem().getUserId() != ownerId) {
            log.warn("Данный пользователь не является владельцем вещи: пользователь {}, {}",
                    ownerId, booking);
            throw new ConditionsNotMetException("Данный пользователь не является владельцем вещи");
        }
    }

    private void isOwnerOrBooker(Booking booking, int ownerId, int bookerId) {
        if (booking.getItem().getUserId() != ownerId && booking.getBooker().getId() != bookerId) {
            log.warn("Данный пользователь не является владельцем или заказчиком вещи: пользователь {}, {}", ownerId, booking);
            throw new ConditionsNotMetException("Данный пользователь не является владельцем или заказчиком вещи");
        }
    }

    private void isItemAvailable(int itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с таким id не была найдена"));

        if (!item.getAvailable()) {
            throw new ConditionsNotMetException("Вещь недоступна для бронирования");
        }
    }
}
