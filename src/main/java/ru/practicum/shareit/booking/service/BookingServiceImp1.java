package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import ru.practicum.shareit.validation.ValidationUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class BookingServiceImp1 implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Transactional
    public BookingDto createBooking(CreateBookingDto createBookingDto, int userId) {
        log.info("Начинается создание аренды на вещь: запись {}, пользователь {}", createBookingDto, userId);
        ValidationUtils.isExist(userRepository, userId, "Данный пользователь не найден");
        isItemAvailable(createBookingDto.getItemId());
        isEndAfterStar(createBookingDto);

        Booking booking = BookingMapper.fromCreateDto(createBookingDto, userId);
        booking = bookingRepository.save(booking);
        // Второе место, где такая же проблема возникает
        booking.setBooker(userRepository.findById(booking.getBooker().getId()).get());
        booking.setItem(itemRepository.findById(booking.getItem().getId()).get());

        log.info("Создание аренды прошло успешно: {},", booking);
        BookingDto bookingDto = BookingMapper.toDto(booking);
        isStatusWaiting(bookingDto);
        return bookingDto;
    }

    @Transactional
    public BookingDto approveBooking(int bookingId, Boolean approved, int userId) {
        log.info("Начинается изменение статуса записи: запись {}, {}, пользователь {}", bookingId, approved, userId);
        if (approved == null) {
            log.warn("Передан пустой параметр approved");
            throw new ConditionsNotMetException("Передан пустой параметр approved");
        }
        isOwner(bookingId, userId);

        Booking booking = findBookingById(bookingId);
        BookingDto bookingDto = BookingMapper.toDto(booking);

        isStatusWaiting(bookingDto);

        bookingRepository.approveBooking(approved, bookingId);

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
        ValidationUtils.isExist(userRepository, userId, "Данный пользователь не найден");

        List<Booking> bookings = findByState(state, userId, false);
        System.out.println(bookings);

        return BookingMapper.toDto(bookings);
    }

    public List<BookingDto> getOwnBookings(State state, int userId) {
        ValidationUtils.isExist(userRepository, userId, "Данный пользователь не найден");

        List<Booking> bookings = findByState(state, userId, true);

        return BookingMapper.toDto(bookings);
    }

    private List<Booking> findByState(State state, int userId, boolean isOwner) {
        switch (state) {
            case ALL -> {
                if (isOwner) {
                    return bookingRepository.findAllByItemOwnerId(userId);
                } else {
                    return bookingRepository.findAllByBookerId(userId);
                }
            }
            case REJECTED -> {
                if (isOwner) {
                    return bookingRepository.findAllRejectedByItemOwnerId(userId);
                } else {
                    return bookingRepository.findAllRejectedByBookerId(userId);
                }
            }
            case WAITING -> {
                if (isOwner) {
                    return bookingRepository.findAllByUserAndState(null, userId, false, true,
                            true, false, false);
                } else {
                    return bookingRepository.findAllByUserAndState(userId, null, false, true,
                            true, false, false);
                }
            }
            case PAST -> {
                if (isOwner) {
                    return bookingRepository.findAllByUserAndState(null, userId, true, false,
                            false, true, false);
                } else {
                    return bookingRepository.findAllByUserAndState(userId, null, true, false,
                            false, true, false);
                }
            }
            case CURRENT -> {
                if (isOwner) {
                    return bookingRepository.findAllByUserAndState(null, userId, true, false,
                            false, false, true);
                } else {
                    return bookingRepository.findAllByUserAndState(userId, null, true, false,
                            false, false, true);
                }
            }
            case FUTURE -> {
                if (isOwner) {
                    return bookingRepository.findAllByUserAndState(null, userId, true, false,
                            true, false, false);
                } else {
                    return bookingRepository.findAllByUserAndState(userId, null, true, false,
                            true, false, false);
                }
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

    private void isEndAfterStar(CreateBookingDto bookingDto) {
        if (bookingDto.getEnd().isBefore(bookingDto.getStart())) {
            log.warn("Конец бронирования должен быть после его начала: start {}, end {}",
                    bookingDto.getStart(), bookingDto.getEnd());
            throw new ConditionsNotMetException("Конец бронирования должен быть после его начала");
        }
    }

    private void isStatusWaiting(BookingDto bookingDto) {
        if (bookingDto.getStatus() != State.WAITING) {
            log.warn("Бронирование должно быть в статусе ожидания: {}", bookingDto);
            throw new ConditionsNotMetException("Бронирование должно быть в статусе ожидания");
        }
    }
}
