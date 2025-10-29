package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserControllerTest {
    private final ObjectMapper mapper;
    private final MockMvc mvc;

    @MockBean
    UserService userService;

    UserDto firstUserDto;
    UserDto secondUserDto;

    @BeforeEach
    public void setUp() {
        firstUserDto = new UserDto();
        firstUserDto.setId(1);
        firstUserDto.setName("name1");
        firstUserDto.setEmail("email1@mail.ru");

        secondUserDto = new UserDto();
        firstUserDto.setId(2);
        firstUserDto.setName("name2");
        firstUserDto.setEmail("email2@mail.ru");
    }

    @Test
    public void createUserTest() throws Exception {
        CreateUserDto createUserDto = new CreateUserDto();
        createUserDto.setName("name1");
        createUserDto.setEmail("email1@mail.ru");

        when(userService.createUser(createUserDto))
                .thenReturn(firstUserDto);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(createUserDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(firstUserDto.getId()), Integer.class))
                .andExpect(jsonPath("$.email", is(firstUserDto.getEmail())))
                .andExpect(jsonPath("$.name", is(firstUserDto.getName())));
    }

    @Test
    public void getAllUsersTest() throws Exception {
        List<UserDto> userDtoList = List.of(firstUserDto, secondUserDto);

        when(userService.getAllUsers())
                .thenReturn(userDtoList);

        mvc.perform(get("/users")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(firstUserDto.getId()), Integer.class))
                .andExpect(jsonPath("$[0].email", is(firstUserDto.getEmail())))
                .andExpect(jsonPath("$[0].name", is(firstUserDto.getName())))
                .andExpect(jsonPath("$[1].id", is(secondUserDto.getId()), Integer.class))
                .andExpect(jsonPath("$[1].email", is(secondUserDto.getEmail())))
                .andExpect(jsonPath("$[1].name", is(secondUserDto.getName())));
    }

    @Test
    public void getUserTest() throws Exception {

        when(userService.getUser(1))
                .thenReturn(firstUserDto);

        mvc.perform(get("/users/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(firstUserDto.getId()), Integer.class))
                .andExpect(jsonPath("$.email", is(firstUserDto.getEmail())))
                .andExpect(jsonPath("$.name", is(firstUserDto.getName())));
    }

    @Test
    public void updateUserTest() throws Exception {
        when(userService.updateUser(firstUserDto, 1))
                .thenReturn(firstUserDto);

        mvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(firstUserDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(firstUserDto.getId()), Integer.class))
                .andExpect(jsonPath("$.email", is(firstUserDto.getEmail())))
                .andExpect(jsonPath("$.name", is(firstUserDto.getName())));
    }

    @Test
    public void deleteUserTest() throws Exception {
        when(userService.updateUser(firstUserDto, 1))
                .thenReturn(null);

        mvc.perform(delete("/users/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
