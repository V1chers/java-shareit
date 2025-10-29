package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.exception.exceptions.ConflictException;
import ru.practicum.shareit.exception.exceptions.NotFoundException;
import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class UserServiceTest {

    @Autowired
    UserService userService;

    @Test
    public void getUserTest() {
        UserDto userDto = userService.getUser(1);

        assertThat(userDto.getId(), equalTo(1));
        assertThat(userDto.getName(), equalTo("name1"));
        assertThat(userDto.getEmail(), equalTo("name1@mail.ru"));
    }

    @Test
    public void getAllUsersTest() {
        List<UserDto> userDtoList = userService.getAllUsers();

        assertThat(userDtoList.size(), greaterThanOrEqualTo(5));

        UserDto userDto = userDtoList.getFirst();

        assertThat(userDto.getId(), equalTo(1));
        assertThat(userDto.getName(), equalTo("name1"));
        assertThat(userDto.getEmail(), equalTo("name1@mail.ru"));
    }

    @Test
    public void updateUserTest() {
        UserDto userDto = userService.getUser(2);
        userDto.setName("randomName123");
        userDto.setEmail("randoEmail@mail.ru");

        UserDto updatedUserDto = userService.updateUser(userDto, userDto.getId());

        assertThat(updatedUserDto.getId(), equalTo(userDto.getId()));
        assertThat(updatedUserDto.getName(), equalTo(userDto.getName()));
        assertThat(updatedUserDto.getEmail(), equalTo(userDto.getEmail()));
    }

    @Test
    public void createAndDeleteUserTest() {
        // тест добавления
        CreateUserDto createUserDto = new CreateUserDto();
        createUserDto.setEmail("oleg@gmail.com");
        createUserDto.setName("oleg");

        UserDto userDto = userService.createUser(createUserDto);

        assertThat(userDto.getId(), notNullValue());
        assertThat(userDto.getName(), equalTo(createUserDto.getName()));
        assertThat(userDto.getEmail(), equalTo(createUserDto.getEmail()));

        // тест удаления
        UserDto userDtoToDelete = userService.getUser(userDto.getId());
        assertThat(userDtoToDelete, notNullValue());

        userService.deleteUser(userDtoToDelete.getId());
        try {
            userService.getUser(userDtoToDelete.getId());
        } catch (NotFoundException e) {
            assertThat(e.getClass(), equalTo(NotFoundException.class));
        }
    }

    @Test
    public void shouldCreateConflictException() {
        // тест добавления
        CreateUserDto createUserDto = new CreateUserDto();
        createUserDto.setEmail("oleg123@gmail.com");
        createUserDto.setName("oleg123");

        CreateUserDto createUserDto2 = new CreateUserDto();
        createUserDto2.setEmail("oleg123@gmail.com");
        createUserDto2.setName("oleg1233");

        UserDto userDto = userService.createUser(createUserDto);

        try {
            userService.createUser(createUserDto2);
        } catch (ConflictException e) {
            assertThat(e.getClass(), equalTo(ConflictException.class));
        }
    }
}
