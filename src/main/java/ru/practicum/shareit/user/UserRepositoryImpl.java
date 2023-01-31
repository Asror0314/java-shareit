package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.AlreadyExistsException;
import ru.practicum.shareit.exception.NotFoundException;

import javax.validation.ValidationException;
import java.nio.file.FileAlreadyExistsException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        final User user = users.get(userId);
       if(user == null) {
           throw new NotFoundException(String.format("User id = '%d' not found", userId));
       }
       return Optional.of(user);
    }

    @Override
    public Optional<User> addNewUser(final User user) {
        if(user.getEmail() == null) {
            throw new ValidationException(String.format("Please enter the user's email", user.getEmail()));
        }

        isEmailExists(user);

        user.setId(generatedId());
        users.put(user.getId(), user);

        return Optional.of(user);
    }

    @Override
    public Optional<User> updateUser(final User updateUser, final Long userId) {
        final User user = findUserById(userId).get();
        isEmailExists(updateUser);

        if(updateUser.getName() != null) {
            user.setName(updateUser.getName());
        }
        if(updateUser.getEmail() != null) {
            user.setEmail(updateUser.getEmail());
        }
        users.put(userId, user);

        return Optional.of(user);
    }

    @Override
    public void deleteUser(final Long userId) {
        users.remove(userId);
    }

    private Long generatedId() {
        return ++generatedId;
    }

    private void isEmailExists(User user) {
        if(users.values().stream().map(User::getEmail).anyMatch(users -> users.equals(user.getEmail()))) {
            throw new AlreadyExistsException(String.format("User email = '%s' already exists", user.getEmail()));
        }
    }

}
