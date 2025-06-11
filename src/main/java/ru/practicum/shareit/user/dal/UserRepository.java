package ru.practicum.shareit.user.dal;

import ru.practicum.shareit.user.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    User createUser(User user);

    Optional<User> getUser(int userId);

    List<User> getAllUsers();

    User updateUser(User user);

    void deleteUser(int userId);
}
