package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.exceptions.ConditionsNotMetException;
import ru.practicum.shareit.exception.exceptions.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dal.UserRepository;
import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImp1 implements UserService {
    private final UserRepository userRepository;

    public UserDto createUser(CreateUserDto userDto) {
        log.info("начинаем создания пользователя: {}", userDto);
        isEmailAlreadyExists(userDto.getEmail());

        User user = UserMapper.fromCreateDto(userDto);
        user = userRepository.createUser(user);

        log.info("Пользователь создан: {}", user);
        return UserMapper.toDto(user);
    }

    public UserDto getUser(int userId) {
        Optional<User> user = userRepository.getUser(userId);

        if (user.isPresent()) {
            return UserMapper.toDto(user.get());
        } else {
            throw new NotFoundException("Пользователь с данным id не был найден");
        }
    }

    public List<UserDto> getAllUsers() {
        List<User> users = userRepository.getAllUsers();

        return UserMapper.toDto(users);
    }

    public UserDto updateUser(UserDto userDto, int userId) {
        log.info("начинаем обновление данных пользователя: {}, {}", userId, userDto);
        isUserExists(userId);
        if (userDto.getEmail() != null) {
            isEmailAlreadyExists(userDto.getEmail());
        }

        userDto.setId(userId);
        User user = UserMapper.fromDto(userDto);
        user = userRepository.updateUser(user);

        log.info("Данные пользователя обновлены: {}", user);
        return UserMapper.toDto(user);
    }

    public void deleteUser(int userId) {
        isUserExists(userId);

        userRepository.deleteUser(userId);
    }

    private void isUserExists(int userId) {
        getUser(userId);
    }

    private void isEmailAlreadyExists(String email) {
        List<String> emails = userRepository.getAllUsers().stream()
                .map(User::getEmail)
                .toList();

        if (emails.contains(email)) {
            log.warn("Выданная электронная почта занята: {}", email);
            throw new ConditionsNotMetException("Данная электронная почта уже занята");
        }
    }
}
