package ru.practicum.shareit.user;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserRepository {

    List<User> findAllUsers();

    Optional<User> findUserById(final Long userId);

    Optional<User> addNewUser(final User user);

    Optional<User> updateUser(final User user, final Long userId);

    void deleteUser(final Long userId);
}
