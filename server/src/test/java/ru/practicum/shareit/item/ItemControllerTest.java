package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemControllerTest {
    private final ObjectMapper mapper;
    private final MockMvc mvc;

    @MockBean
    ItemService itemService;

    ItemDto firstItemDto;
    ItemDto secondItemDto;
    ItemCommentsDto firstItemCommentsDto;
    ItemCommentsDto secondItemCommentsDto;

    @BeforeEach
    public void setUp() {
        firstItemDto = new ItemDto();
        firstItemDto.setId(1);
        firstItemDto.setName("name1");
        firstItemDto.setDescription("desc1");
        firstItemDto.setAvailable(true);

        secondItemDto = new ItemDto();
        secondItemDto.setId(2);
        secondItemDto.setName("name2");
        secondItemDto.setDescription("desc2");
        secondItemDto.setAvailable(true);

        firstItemCommentsDto = new ItemCommentsDto();
        firstItemCommentsDto.setId(2);
        firstItemCommentsDto.setName("name2");
        firstItemCommentsDto.setDescription("desc2");
        firstItemCommentsDto.setAvailable(true);

        secondItemCommentsDto = new ItemCommentsDto();
        secondItemCommentsDto.setId(2);
        secondItemCommentsDto.setName("name2");
        secondItemCommentsDto.setDescription("desc2");
        secondItemCommentsDto.setAvailable(true);
    }

    @Test
    public void createItemTest() throws Exception {
        CreateItemDto createItemDto = new CreateItemDto();
        createItemDto.setName(firstItemDto.getName());
        createItemDto.setDescription(firstItemDto.getDescription());
        createItemDto.setAvailable(firstItemDto.getAvailable());

        when(itemService.createItem(createItemDto, 1))
                .thenReturn(firstItemDto);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(createItemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(firstItemDto.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(firstItemDto.getName())))
                .andExpect(jsonPath("$.description", is(firstItemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(firstItemDto.getAvailable()), Boolean.class));
    }

    @Test
    public void updateItemTest() throws Exception {
        when(itemService.updateItem(firstItemDto, 1, 1))
                .thenReturn(firstItemDto);

        mvc.perform(patch("/items/1")
                        .content(mapper.writeValueAsString(firstItemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(firstItemDto.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(firstItemDto.getName())))
                .andExpect(jsonPath("$.description", is(firstItemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(firstItemDto.getAvailable()), Boolean.class));
    }

    @Test
    public void getItemTest() throws Exception {
        when(itemService.getItem(1))
                .thenReturn(firstItemCommentsDto);

        mvc.perform(get("/items/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(firstItemCommentsDto.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(firstItemCommentsDto.getName())))
                .andExpect(jsonPath("$.description", is(firstItemCommentsDto.getDescription())))
                .andExpect(jsonPath("$.available", is(firstItemCommentsDto.getAvailable()), Boolean.class));
    }

    @Test
    public void getUserItemsTest() throws Exception {
        List<ItemCommentsDto> itemCommentsDtoList = List.of(firstItemCommentsDto, secondItemCommentsDto);

        when(itemService.getUserItems(1))
                .thenReturn(itemCommentsDtoList);

        mvc.perform(get("/items")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(firstItemCommentsDto.getId()), Integer.class))
                .andExpect(jsonPath("$[0].name", is(firstItemCommentsDto.getName())))
                .andExpect(jsonPath("$[0].description", is(firstItemCommentsDto.getDescription())))
                .andExpect(jsonPath("$[0].available", is(firstItemCommentsDto.getAvailable()), Boolean.class))
                .andExpect(jsonPath("$[1].id", is(secondItemDto.getId()), Integer.class))
                .andExpect(jsonPath("$[1].name", is(secondItemDto.getName())))
                .andExpect(jsonPath("$[1].description", is(secondItemDto.getDescription())))
                .andExpect(jsonPath("$[1].available", is(secondItemDto.getAvailable()), Boolean.class));
    }

    @Test
    public void searchItemsTest() throws Exception {
        List<ItemDto> itemDtoList = List.of(firstItemDto, secondItemDto);

        when(itemService.searchItems("name"))
                .thenReturn(itemDtoList);

        mvc.perform(get("/items/search?text=name")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(firstItemDto.getId()), Integer.class))
                .andExpect(jsonPath("$[0].name", is(firstItemDto.getName())))
                .andExpect(jsonPath("$[0].description", is(firstItemDto.getDescription())))
                .andExpect(jsonPath("$[0].available", is(firstItemDto.getAvailable()), Boolean.class))
                .andExpect(jsonPath("$[1].id", is(secondItemDto.getId()), Integer.class))
                .andExpect(jsonPath("$[1].name", is(secondItemDto.getName())))
                .andExpect(jsonPath("$[1].description", is(secondItemDto.getDescription())))
                .andExpect(jsonPath("$[1].available", is(secondItemDto.getAvailable()), Boolean.class));
    }

    @Test
    public void addCommentTest() throws Exception {
        TextDto textDto = new TextDto();
        textDto.setText("Text");

        CommentDto commentDto = new CommentDto();
        commentDto.setId(1);
        commentDto.setAuthorName("Vasya");
        commentDto.setItemId(1);
        commentDto.setText(textDto.getText());

        when(itemService.addComment(textDto, 1, 1))
                .thenReturn(commentDto);

        mvc.perform(post("/items/1/comment")
                        .content(mapper.writeValueAsString(textDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDto.getId()), Integer.class))
                .andExpect(jsonPath("$.text", is(commentDto.getText())))
                .andExpect(jsonPath("$.itemId", is(commentDto.getItemId()), Integer.class))
                .andExpect(jsonPath("$.authorName", is(commentDto.getAuthorName())));
    }
}
