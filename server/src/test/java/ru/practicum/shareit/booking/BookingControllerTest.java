package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.enums.State;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingControllerTest {
    private final ObjectMapper mapper;
    private final MockMvc mvc;

    @MockBean
    BookingService bookingService;

    BookingDto firstBookingDto;
    BookingDto secondBookingDto;

    @BeforeEach
    public void setUp() {
        firstBookingDto = new BookingDto();
        firstBookingDto.setStart(LocalDateTime.now().plusHours(1));
        firstBookingDto.setEnd(LocalDateTime.now().plusHours(2));
        firstBookingDto.setStatus(State.WAITING);

        ItemDto itemDto = new ItemDto();
        itemDto.setId(1);
        firstBookingDto.setItem(itemDto);

        secondBookingDto = new BookingDto();
        secondBookingDto.setStart(LocalDateTime.now().plusHours(3));
        secondBookingDto.setEnd(LocalDateTime.now().plusHours(4));
        secondBookingDto.setStatus(State.WAITING);
    }

    @Test
    public void createBookingTest() throws Exception {
        CreateBookingDto createBookingDto = new CreateBookingDto();
        createBookingDto.setStart(firstBookingDto.getStart().truncatedTo(ChronoUnit.SECONDS));
        createBookingDto.setEnd(firstBookingDto.getEnd().truncatedTo(ChronoUnit.SECONDS));
        createBookingDto.setItemId(firstBookingDto.getItem().getId());

        when(bookingService.createBooking(createBookingDto, 1))
                .thenReturn(firstBookingDto);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(createBookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(firstBookingDto.getId()), Integer.class))
                .andExpect(jsonPath("$.status", is(firstBookingDto.getStatus().toString())))
                .andExpect(jsonPath("$.item.id", is(firstBookingDto.getItem().getId()), Integer.class));
    }

    @Test
    public void approveBookingTest() throws Exception {
        when(bookingService.approveBooking(1, true, 1))
                .thenReturn(firstBookingDto);

        mvc.perform(patch("/bookings/1?approved=true")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(firstBookingDto.getId()), Integer.class))
                .andExpect(jsonPath("$.status", is(firstBookingDto.getStatus().toString())))
                .andExpect(jsonPath("$.item.id", is(firstBookingDto.getItem().getId()), Integer.class));
    }

    @Test
    public void getBookingTest() throws Exception {
        when(bookingService.getBooking(1, 1))
                .thenReturn(firstBookingDto);

        mvc.perform(get("/bookings/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(firstBookingDto.getId()), Integer.class))
                .andExpect(jsonPath("$.status", is(firstBookingDto.getStatus().toString())))
                .andExpect(jsonPath("$.item.id", is(firstBookingDto.getItem().getId()), Integer.class));
    }

    @Test
    public void getBookingsTest() throws Exception {
        List<BookingDto> bookingDtoList = List.of(firstBookingDto, secondBookingDto);

        when(bookingService.getBookings(State.ALL, 1))
                .thenReturn(bookingDtoList);

        mvc.perform(get("/bookings")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(firstBookingDto.getId()), Integer.class))
                .andExpect(jsonPath("$[0].status", is(firstBookingDto.getStatus().toString())))
                .andExpect(jsonPath("$[0].item.id", is(firstBookingDto.getItem().getId()), Integer.class))
                .andExpect(jsonPath("$[1].id", is(secondBookingDto.getId()), Integer.class))
                .andExpect(jsonPath("$[1].status", is(secondBookingDto.getStatus().toString())));
    }

    @Test
    public void getOwnBookingsTest() throws Exception {
        List<BookingDto> bookingDtoList = List.of(firstBookingDto, secondBookingDto);

        when(bookingService.getOwnBookings(State.ALL, 1))
                .thenReturn(bookingDtoList);

        mvc.perform(get("/bookings/owner")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(firstBookingDto.getId()), Integer.class))
                .andExpect(jsonPath("$[0].status", is(firstBookingDto.getStatus().toString())))
                .andExpect(jsonPath("$[0].item.id", is(firstBookingDto.getItem().getId()), Integer.class))
                .andExpect(jsonPath("$[1].id", is(secondBookingDto.getId()), Integer.class))
                .andExpect(jsonPath("$[1].status", is(secondBookingDto.getStatus().toString())));
    }
}
