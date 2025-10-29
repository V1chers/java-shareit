package ru.practicum.shareit.itemrequest;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.AnswerDto;
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.request.dto.DescriptionDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestControllerTest {
    private final ObjectMapper mapper;
    private final MockMvc mvc;
    @MockBean
    ItemRequestService itemRequestService;

    DescriptionDto descriptionDto;
    ItemRequestDto firstItemRequestDto;
    ItemRequestDto secondItemRequestDto;

    @BeforeEach
    public void createItemRequest() {
        descriptionDto = createDescriptionDto("qwerty");
        firstItemRequestDto = createItemRequestDto(1, descriptionDto.getDescription());
        secondItemRequestDto = createItemRequestDto(2, "123");
    }

    @Test
    public void createRequestTest() throws Exception {
        when(itemRequestService.createRequest(1, descriptionDto))
                .thenReturn(firstItemRequestDto);

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(descriptionDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(firstItemRequestDto.getId()), Integer.class))
                .andExpect(jsonPath("$.created", is(notNullValue())))
                .andExpect(jsonPath("$.description", is(firstItemRequestDto.getDescription())))
                .andExpect(jsonPath("$.items", hasSize(1)));
    }

    @Test
    public void getRequestsTest() throws Exception {
        ItemRequestDto itemRequestDto = createItemRequestDto(2, "123");

        List<ItemRequestDto> requestList = List.of(itemRequestDto, firstItemRequestDto);

        when(itemRequestService.getRequests(1))
                .thenReturn(requestList);

        mvc.perform(get("/requests")
                        .content(mapper.writeValueAsString(descriptionDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[1].id", is(firstItemRequestDto.getId()), Integer.class))
                .andExpect(jsonPath("$[1].created", is(notNullValue())))
                .andExpect(jsonPath("$[1].description", is(firstItemRequestDto.getDescription())))
                .andExpect(jsonPath("$[1].items", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemRequestDto.getId()), Integer.class))
                .andExpect(jsonPath("$[0].created", is(notNullValue())))
                .andExpect(jsonPath("$[0].description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$[0].items", hasSize(1)));
    }

    @Test
    public void getAllRequestsTest() throws Exception {
        List<ItemRequestDto> requestList = List.of(secondItemRequestDto, firstItemRequestDto);

        when(itemRequestService.getAllRequests(1))
                .thenReturn(requestList);

        mvc.perform(get("/requests/all")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[1].id", is(firstItemRequestDto.getId()), Integer.class))
                .andExpect(jsonPath("$[1].created", is(notNullValue())))
                .andExpect(jsonPath("$[1].description", is(firstItemRequestDto.getDescription())))
                .andExpect(jsonPath("$[1].items", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(secondItemRequestDto.getId()), Integer.class))
                .andExpect(jsonPath("$[0].created", is(notNullValue())))
                .andExpect(jsonPath("$[0].description", is(secondItemRequestDto.getDescription())))
                .andExpect(jsonPath("$[0].items", hasSize(1)));
    }

    @Test
    public void getRequestTest() throws Exception {
        when(itemRequestService.getRequest(2))
                .thenReturn(firstItemRequestDto);

        mvc.perform(get("/requests/2")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(firstItemRequestDto.getId()), Integer.class))
                .andExpect(jsonPath("$.created", is(notNullValue())))
                .andExpect(jsonPath("$.description", is(firstItemRequestDto.getDescription())))
                .andExpect(jsonPath("$.items", hasSize(1)));
    }

    private ItemRequestDto createItemRequestDto(int id, String description) {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(id);
        itemRequestDto.setDescription(description);
        itemRequestDto.setCreated(LocalDateTime.now());

        AnswerDto answer = new AnswerDto();
        answer.setId(id);
        answer.setName(description);
        answer.setUserId(1);

        itemRequestDto.setItems(List.of(answer));

        return itemRequestDto;
    }

    private DescriptionDto createDescriptionDto(String description) {
        DescriptionDto descriptionDto = new DescriptionDto();
        descriptionDto.setDescription(description);

        return descriptionDto;
    }
}
