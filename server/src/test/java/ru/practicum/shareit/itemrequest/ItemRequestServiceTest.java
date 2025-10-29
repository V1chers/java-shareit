package ru.practicum.shareit.itemrequest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.item.dto.AnswerDto;
import ru.practicum.shareit.request.dto.DescriptionDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@ActiveProfiles("test")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestServiceTest {

    private final ItemRequestService itemRequestService;

    private final UserService userService;

    @Test
    public void createTransactionTest() {
        UserDto userDto = createUser("123@mail.ru");

        DescriptionDto descriptionDto = new DescriptionDto();
        descriptionDto.setDescription("awdsadwds");

        ItemRequestDto itemRequestDto = itemRequestService.createRequest(userDto.getId(), descriptionDto);

        assertThat(itemRequestDto.getId(), notNullValue());
        assertThat(itemRequestDto.getDescription(), equalTo(descriptionDto.getDescription()));
        assertThat(itemRequestDto.getCreated(), notNullValue());
    }

    @Test
    public void getRequestTest() {
        ItemRequestDto itemRequestDto = itemRequestService.getRequest(1);

        assertThat(itemRequestDto.getId(), is(1));
        assertThat(itemRequestDto.getDescription(), is("desc1"));

        LocalDateTime created = LocalDateTime.of(2025, 6, 15, 20, 0);
        ZonedDateTime createdZonedDateTime = created.atZone(ZoneId.of("UTC"));
        LocalDateTime createdBySystemTime = createdZonedDateTime.withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();

        assertThat(itemRequestDto.getCreated(), is(createdBySystemTime));
        assertThat(itemRequestDto.getItems(), hasSize(1));

        AnswerDto answerDto = itemRequestDto.getItems().getFirst();
        assertThat(answerDto.getId(), equalTo(1));
        assertThat(answerDto.getName(), equalTo("item1"));
        assertThat(answerDto.getUserId(), equalTo(3));
    }

    @Test
    public void getAllRequestsTest() {
        List<ItemRequestDto> itemRequestDtoList = itemRequestService.getRequests(2);

        assertThat(itemRequestDtoList, hasSize(2));
    }

    @Test
    public void getAllRequestsExceptOwn() {
        List<ItemRequestDto> itemRequestDtoList = itemRequestService.getAllRequests(1);

        assertThat(itemRequestDtoList.size(), greaterThanOrEqualTo(3));
    }

    private UserDto createUser(String email) {
        CreateUserDto createUserDto = new CreateUserDto();
        createUserDto.setEmail(email);
        createUserDto.setName("oleg");

        return userService.createUser(createUserDto);
    }
}
