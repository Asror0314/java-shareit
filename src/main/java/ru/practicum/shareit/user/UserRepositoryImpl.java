package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private final Map<Long, User> users = new HashMap<>();
    private long generatedId = 0;

    @Override
    public List<User> findAllUsers() {
        return users.values().stream().collect(Collectors.toList());
    }

    @Override
    public Optional<User> findUserById(final Long userId) {
       if(!users.containsKey(userId)) {
           throw new NotFoundException(String.format("User id = '%d' not found", userId));
       }

       User user = users.get(userId);
       return Optional.of(user);
    }

    @Override
    public Optional<User> addNewUser(final User user) {
        user.setId(generatedId());
        users.put(user.getId(), user);

        return Optional.of(user);
    }

    @Override
    public Optional<User> updateUser(final User newUser) {
        users.put(newUser.getId(), newUser);

        return Optional.of(newUser);
    }

    @Override
    public void deleteUser(final Long userId) {
        users.remove(userId);
    }

    private Long generatedId() {
        return ++generatedId;
    }



}
