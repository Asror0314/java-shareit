package ru.practicum.shareit.user;

import java.util.List;

public interface UserService {

    List<User> getAllUsers();

    User getUserById(final Long userId);

    User addUser(final User user);

    User updateUser(final User newUser, final Long userId);

    void deleteUser(final Long userId);

}
