package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.exceptions.ConditionsNotMetException;
import ru.practicum.shareit.exception.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

@SpringBootTest
@ActiveProfiles("test")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceTest {

    private final ItemService itemService;

    private final BookingService bookingService;

    @Test
    public void getItemTest() {
        ItemCommentsDto itemCommentsDto = itemService.getItem(1);

        assertThat(itemCommentsDto.getId(), is(1));
        assertThat(itemCommentsDto.getName(), is("item1"));
        assertThat(itemCommentsDto.getDescription(), is("desc1"));
        assertThat(itemCommentsDto.getAvailable(), is(true));
        assertThat(itemCommentsDto.getLastBooking(), notNullValue());
        assertThat(itemCommentsDto.getComments(), hasSize(1));

        BookingDto lastBooking = itemCommentsDto.getLastBooking();

        assertThat(lastBooking.getBooker().getId(), is(1));
        assertThat(lastBooking.getItem().getId(), is(1));

        List<CommentDto> comments = itemCommentsDto.getComments();

        assertThat(comments, hasSize(1));
        assertThat(comments.getFirst().getId(), notNullValue());
        assertThat(comments.getFirst().getText(), is("comment1"));
        assertThat(comments.getFirst().getAuthorName(), is("name1"));
    }

    @Test
    public void getUserItemsTest() {
        List<ItemCommentsDto> itemList = itemService.getUserItems(3);

        assertThat(itemList, hasSize(3));
        assertThat(itemList.getFirst().getId(), is(1));
        assertThat(itemList.getFirst().getName(), is("item1"));
        assertThat(itemList.getFirst().getDescription(), is("desc1"));
        assertThat(itemList.getFirst().getAvailable(), is(true));
        assertThat(itemList.getFirst().getLastBooking(), notNullValue());
        assertThat(itemList.getFirst().getComments(), hasSize(1));
        assertThat(itemList.get(1).getComments(), hasSize(2));
    }

    @Test
    public void searchItemsTest() {
        List<ItemDto> itemListByName = itemService.searchItems("em1");
        assertThat(itemListByName, hasSize(1));
        assertThat(itemListByName.getFirst().getName(), is("item1"));
        assertThat(itemListByName.getFirst().getDescription(), is("desc1"));

        List<ItemDto> itemListByDescription = itemService.searchItems("sc3");
        assertThat(itemListByDescription, hasSize(1));
        assertThat(itemListByDescription.getFirst().getName(), is("item3"));
        assertThat(itemListByDescription.getFirst().getDescription(), is("desc3"));
    }

    @Test
    public void createAndUpdateItemTest() {
        // тест создания вещи
        CreateItemDto createItemDto = createItemDto("RandomDescription", "RandomName", true);

        ItemDto itemDto = itemService.createItem(createItemDto, 4);

        assertThat(itemDto.getId(), notNullValue());
        assertThat(itemDto.getName(), equalTo(createItemDto.getName()));
        assertThat(itemDto.getDescription(), equalTo(createItemDto.getDescription()));
        assertThat(itemDto.getAvailable(), equalTo(createItemDto.getAvailable()));

        // тест обновления данных вещи

        ItemDto itemDtoToUpdate = new ItemDto();
        itemDtoToUpdate.setName("OtherName");
        itemDtoToUpdate.setDescription("OtherDescription");
        itemDtoToUpdate.setAvailable(false);

        ItemDto updatedItem = itemService.updateItem(itemDtoToUpdate, itemDto.getId(), 4);

        assertThat(updatedItem.getId(), equalTo(itemDto.getId()));
        assertThat(updatedItem.getName(), equalTo(itemDtoToUpdate.getName()));
        assertThat(updatedItem.getDescription(), equalTo(itemDtoToUpdate.getDescription()));
        assertThat(updatedItem.getAvailable(), equalTo(itemDtoToUpdate.getAvailable()));
    }

    @Test
    public void addCommentTest() {
        TextDto textDto = new TextDto();
        textDto.setText("RandomText");

        CommentDto commentDto = itemService.addComment(textDto, 4, 5);

        assertThat(commentDto.getId(), notNullValue());
        assertThat(commentDto.getText(), equalTo(textDto.getText()));
        assertThat(commentDto.getItemId(), equalTo(5));
        assertThat(commentDto.getAuthorName(), equalTo("name4"));
        assertThat(commentDto.getCreated(), notNullValue());
    }

    @Test
    public void shouldThrowCreateItemWithUnknownUser() {
        CreateItemDto createItemDto = createItemDto("RandomDescription2", "RandomName2", true);

        try {
            itemService.createItem(createItemDto, 9999);
        } catch (NotFoundException e) {
            assertThat(e.getClass(), is(NotFoundException.class));
            assertThat(e.getMessage(), is("Данный пользователь не найден: 9999"));
        }
    }

    private CreateItemDto createItemDto(String desc, String name, Boolean available) {
        CreateItemDto createItemDto = new CreateItemDto();
        createItemDto.setDescription(desc);
        createItemDto.setName(name);
        createItemDto.setAvailable(available);

        return createItemDto;
    }

    @Test
    public void shouldThrowAddCommentNotBookedUser() {
        TextDto textDto = new TextDto();
        textDto.setText("RandomText");

        try {
            CommentDto commentDto = itemService.addComment(textDto, 6, 5);
        } catch (ConditionsNotMetException e) {
            assertThat(e.getClass(), is(ConditionsNotMetException.class));
            assertThat(e.getMessage(), is("Пользователь не заказывал данный товар"));
        }
    }

    @Test
    public void shouldThrowAddCommentNotUsedItem() {
        CreateBookingDto createBookingDto = new CreateBookingDto();
        createBookingDto.setItemId(4);
        createBookingDto.setStart(LocalDateTime.now().plusHours(10));
        createBookingDto.setEnd(LocalDateTime.now().plusHours(10));

        BookingDto bookingDto = bookingService.createBooking(createBookingDto, 5);

        bookingService.approveBooking(bookingDto.getId(), true, 4);

        TextDto textDto = new TextDto();
        textDto.setText("RandomText2");

        try {
            CommentDto commentDto = itemService.addComment(textDto, 5, 4);
        } catch (ConditionsNotMetException e) {
            assertThat(e.getClass(), is(ConditionsNotMetException.class));
            assertThat(e.getMessage(), is("Пользователь не успел воспользоваться товаром"));
        }
    }
}
