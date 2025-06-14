package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.exceptions.ConflictException;
import ru.practicum.shareit.exception.exceptions.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dal.UserRepository;
import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.validation.ValidationService;

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
        user = userRepository.save(user);

        log.info("Пользователь создан: {}", user);
        return UserMapper.toDto(user);
    }

    public UserDto getUser(int userId) {
        Optional<User> user = userRepository.findById(userId);

        if (user.isPresent()) {
            return UserMapper.toDto(user.get());
        } else {
            throw new NotFoundException("Пользователь с данным id не был найден");
        }
    }

    public List<UserDto> getAllUsers() {
        List<User> users = userRepository.findAll();

        return UserMapper.toDto(users);
    }

    public UserDto updateUser(UserDto userDto, int userId) {
        log.info("начинаем обновление данных пользователя: {}, {}", userId, userDto);
        if (userDto.getEmail() != null) {
            isEmailAlreadyExists(userDto.getEmail());
        }

        User updatingUser = UserMapper.fromDto(getUser(userId));
        User user = UserMapper.fromDto(userDto);

        if (user.getName() != null) {
            updatingUser.setName(user.getName());
        }
        if (user.getEmail() != null) {
            updatingUser.setEmail(user.getEmail());
        }

        user = userRepository.save(updatingUser);

        log.info("Данные пользователя обновлены: {}", user);
        return UserMapper.toDto(user);
    }

    public void deleteUser(int userId) {
        ValidationService.isExist(userRepository, userId, "Данный пользователь не найден");

        userRepository.deleteById(userId);
    }

    private void isEmailAlreadyExists(String email) {
        Optional<User> userWithEmail = userRepository.findByEmail(email);

        if (userWithEmail.isPresent()) {
            log.warn("Выданная электронная почта занята: {}", email);
            throw new ConflictException("Данная электронная почта уже занята");
        }
    }
}
