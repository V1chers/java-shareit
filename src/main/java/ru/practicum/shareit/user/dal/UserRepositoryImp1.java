package ru.practicum.shareit.user.dal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserRepositoryImp1 {
    private final HashMap<Integer, User> users;

    public User createUser(User user) {
        int id = createId();

        user.setId(id);
        users.put(id, user);

        return user;
    }

    public Optional<User> getUser(int userId) {
        return Optional.ofNullable(users.get(userId));
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    public User updateUser(User user) {
        User updatingUser = users.get(user.getId());

        if (user.getName() != null) {
            updatingUser.setName(user.getName());
        }
        if (user.getEmail() != null) {
            updatingUser.setEmail(user.getEmail());
        }

        return updatingUser;
    }

    public void deleteUser(int userId) {
        users.remove(userId);
    }

    private Integer createId() {
        Optional<Integer> maxUserId = users.keySet().stream()
                .max(Integer::compare);

        return maxUserId.orElse(0) + 1;
    }
}
