package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.enums.State;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.exceptions.ConditionsNotMetException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

@SpringBootTest
@ActiveProfiles("test")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceTest {

    private final BookingService bookingService;

    @Test
    public void createAndApproveBookingTest() {
        //создание записи
        CreateBookingDto createBookingDto = new CreateBookingDto();
        createBookingDto.setItemId(4);
        createBookingDto.setStart(LocalDateTime.now().plusHours(10));
        createBookingDto.setEnd(LocalDateTime.now().plusHours(11));

        BookingDto bookingDto = bookingService.createBooking(createBookingDto, 5);

        assertThat(bookingDto.getId(), notNullValue());
        assertThat(bookingDto.getStart(), equalTo(createBookingDto.getStart()));
        assertThat(bookingDto.getStart(), equalTo(createBookingDto.getStart()));
        assertThat(bookingDto.getStatus(), is(State.WAITING));

        ItemDto item = bookingDto.getItem();

        assertThat(item.getId(), is(4));
        assertThat(item.getName(), is("item4"));
        assertThat(item.getDescription(), is("desc4"));

        UserDto user = bookingDto.getBooker();

        assertThat(user.getId(), is(5));
        assertThat(user.getName(), is("name5"));
        assertThat(user.getEmail(), is("name5@mail.ru"));

        //подтверждение записи
        BookingDto approvedBooking = bookingService.approveBooking(bookingDto.getId(), true, 4);

        assertThat(approvedBooking.getId(), equalTo(bookingDto.getId()));
        assertThat(approvedBooking.getStatus(), is(State.APPROVED));
        assertThat(bookingDto.getBooker(), notNullValue());
        assertThat(bookingDto.getItem(), notNullValue());
    }

    @Test
    public void getBookingTest() {
        BookingDto bookingDto = bookingService.getBooking(1, 1);
        BookingDto bookingDto2 = bookingService.getBooking(1, 3);

        assertThat(bookingDto2, equalTo(bookingDto));
        assertThat(bookingDto.getId(), is(1));
        assertThat(bookingDto.getStatus(), is(State.PAST));

        LocalDateTime start = LocalDateTime.of(2025, 6, 16, 20, 0);
        ZonedDateTime startZonedDateTime = start.atZone(ZoneId.of("UTC"));
        LocalDateTime startBySystemTime = startZonedDateTime.withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();

        assertThat(bookingDto.getStart(), is(startBySystemTime));

        LocalDateTime end = LocalDateTime.of(2025, 6, 16, 21, 0);
        ZonedDateTime endZonedDateTime = end.atZone(ZoneId.of("UTC"));
        LocalDateTime endBySystemTime = endZonedDateTime.withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();

        assertThat(bookingDto.getEnd(), is(endBySystemTime));

        ItemDto itemDto = bookingDto.getItem();

        assertThat(itemDto.getId(), is(1));
        assertThat(itemDto.getName(), is("item1"));
        assertThat(itemDto.getDescription(), is("desc1"));

        UserDto booker = bookingDto.getBooker();

        assertThat(booker.getId(), is(1));
        assertThat(booker.getName(), is("name1"));
        assertThat(booker.getEmail(), is("name1@mail.ru"));
    }

    @Test
    public void shouldThrowByGettingRandomUser() {
        try {
            BookingDto bookingDto = bookingService.getBooking(1, 2);
        } catch (ConditionsNotMetException e) {
            assertThat(e.getClass(), is(ConditionsNotMetException.class));
            assertThat(e.getMessage(), is("Данный пользователь не является владельцем или заказчиком вещи"));
        }
    }

    @Test
    public void getBookingsTest() {
        List<BookingDto> bookingDtoList = bookingService.getBookings(State.ALL, 2);

        assertThat(bookingDtoList, hasSize(2));
        assertThat(bookingDtoList.getLast().getId(), is(2));
        assertThat(bookingDtoList.getLast().getStatus(), is(State.REJECTED));
        assertThat(bookingDtoList.getLast().getBooker().getId(), is(2));
        assertThat(bookingDtoList.getLast().getItem().getId(), is(1));
    }

    @Test
    public void getOwnBookingsTest() {
        List<BookingDto> bookingDtoList = bookingService.getOwnBookings(State.ALL, 3);

        assertThat(bookingDtoList, hasSize(4));
        assertThat(bookingDtoList.getFirst().getId(), is(3));
        assertThat(bookingDtoList.getFirst().getStatus(), is(State.PAST));
        assertThat(bookingDtoList.getFirst().getBooker().getId(), is(2));
        assertThat(bookingDtoList.getFirst().getItem().getId(), is(2));
    }

    @Test
    public void getRejectedBookingsTest() {
        List<BookingDto> bookingDtoList = bookingService.getBookings(State.REJECTED, 6);

        assertThat(bookingDtoList, hasSize(1));
        assertThat(bookingDtoList.getFirst().getId(), is(6));
        assertThat(bookingDtoList.getFirst().getStatus(), is(State.REJECTED));
        assertThat(bookingDtoList.getFirst().getBooker().getId(), is(6));
    }

    @Test
    public void getPastBookingsTest() {
        List<BookingDto> bookingDtoList = bookingService.getBookings(State.PAST, 6);

        assertThat(bookingDtoList, hasSize(1));
        assertThat(bookingDtoList.getFirst().getId(), is(7));
        assertThat(bookingDtoList.getFirst().getStatus(), is(State.PAST));
        assertThat(bookingDtoList.getFirst().getBooker().getId(), is(6));
    }

    @Test
    public void getWaitingBookingsTest() {
        BookingDto bookingDto = createBooking(6, LocalDateTime.now().plusHours(20),
                LocalDateTime.now().plusHours(21), 6);

        List<BookingDto> bookingDtoList = bookingService.getBookings(State.WAITING, 6);
        assertThat(bookingDtoList, hasSize(1));
        assertThat(bookingDtoList.getFirst().getId(), is(bookingDto.getId()));
        assertThat(bookingDtoList.getFirst().getStatus(), is(State.WAITING));
        assertThat(bookingDtoList.getFirst().getBooker().getId(), is(6));
    }

    @Test
    public void getFutureBookingsTest() {
        BookingDto bookingDto = createBooking(6, LocalDateTime.now().plusHours(4),
                LocalDateTime.now().plusHours(5), 6);

        bookingService.approveBooking(bookingDto.getId(), true, 5);

        List<BookingDto> bookingDtoList = bookingService.getBookings(State.FUTURE, 6);
        assertThat(bookingDtoList, hasSize(1));
        assertThat(bookingDtoList.getFirst().getId(), is(bookingDto.getId()));
        assertThat(bookingDtoList.getFirst().getStatus(), is(State.FUTURE));
        assertThat(bookingDtoList.getFirst().getBooker().getId(), is(6));
    }

    private BookingDto createBooking(int itemId, LocalDateTime start, LocalDateTime end, int userId) {
        CreateBookingDto createBookingDto = new CreateBookingDto();
        createBookingDto.setItemId(itemId);
        createBookingDto.setStart(start);
        createBookingDto.setEnd(start);

        return bookingService.createBooking(createBookingDto, userId);
    }

    @Test
    public void shouldThrowCreateNotWaitingBooking() {
        try {
            createBooking(6, LocalDateTime.now().minusHours(2), LocalDateTime.now().plusHours(5), 6);
        } catch (ConditionsNotMetException e) {
            assertThat(e.getClass(), is(ConditionsNotMetException.class));
            assertThat(e.getMessage(), is("Бронирование должно быть в статусе ожидания"));
        }
    }

    @Test
    public void shouldThrowAddBookingUnavailableItem() {
        try {
            createBooking(5, LocalDateTime.now().plusHours(4), LocalDateTime.now().plusHours(5), 6);
        } catch (ConditionsNotMetException e) {
            assertThat(e.getClass(), is(ConditionsNotMetException.class));
            assertThat(e.getMessage(), is("Вещь недоступна для бронирования"));
        }
    }

    @Test
    public void shouldThrowGetByRandomUser() {
        try {
            bookingService.getBooking(1, 6);
        } catch (ConditionsNotMetException e) {
            assertThat(e.getClass(), is(ConditionsNotMetException.class));
            assertThat(e.getMessage(), is("Данный пользователь не является владельцем или заказчиком вещи"));
        }
    }

    @Test
    public void shouldThrowApproveByRandomUser() {
        try {
            BookingDto bookingDto = createBooking(7, LocalDateTime.now().plusHours(15),
                    LocalDateTime.now().plusHours(16), 5);
            bookingService.approveBooking(bookingDto.getId(), true, 1);
        } catch (ConditionsNotMetException e) {
            assertThat(e.getClass(), is(ConditionsNotMetException.class));
            assertThat(e.getMessage(), is("Данный пользователь не является владельцем вещи"));
        }
    }
}
