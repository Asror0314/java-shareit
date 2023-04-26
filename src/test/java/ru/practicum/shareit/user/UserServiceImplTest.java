package ru.practicum.shareit.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;


@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository mockUserRepository;
    private UserServiceImpl userService;
    private final User user = new User(1L, "name", "email");
    private final User user2 = new User(2L, "name2", "email2");

    @BeforeEach
    void beforeEach() {
        userService = new UserServiceImpl(mockUserRepository);
    }

    @Test
    void getAllUsers() {
        Mockito
                .when(mockUserRepository.findAll())
                .thenReturn(Arrays.asList(user, user2));

        final List<User> users = userService.getAllUsers();

        Assertions.assertEquals(2, users.size());
        Assertions.assertEquals(user, users.get(0));
        Assertions.assertEquals(user2, users.get(1));
    }

    @Test
    void getAllUsersWhenListEmpty() {
        Mockito
                .when(mockUserRepository.findAll())
                .thenReturn(Arrays.asList());

        final List<User> users = userService.getAllUsers();

        Assertions.assertEquals(0, users.size());
    }

    @Test
    void getUserById() {
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));

        final User userById = userService.getUserById(1L);

        Assertions.assertEquals(user, userById);
        Assertions.assertEquals(user.toString(), userById.toString());
        Assertions.assertEquals(user.hashCode(), userById.hashCode());
    }

    @Test
    void getUserByIdWithWrongUser() {
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenThrow(new NotFoundException("User id = 100 not found"));

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> userService.getUserById(100L));

        Assertions.assertEquals("User id = 100 not found", exception.getMessage());
    }

    @Test
    void addUser() {
        Mockito
                .when(mockUserRepository.save(Mockito.any()))
                .thenReturn(user);

        final User addUser = userService.addUser(user);

        Assertions.assertEquals(user, addUser);
    }

    @Test
    void updateUser() {
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(mockUserRepository.save(Mockito.any()))
                .thenReturn(user);

        final User updateUser = userService.updateUser(user, user.getId());

        Assertions.assertEquals(user, updateUser);
    }

    @Test
    void updateUserWithWrongUser() {
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenThrow(new NotFoundException("User id = 100 not found"));

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> userService.updateUser(user, 100L));

        Assertions.assertEquals("User id = 100 not found", exception.getMessage());
    }

    @Test
    void deleteUser() {
        userService.deleteUser(2L);

        Mockito.verify(mockUserRepository, Mockito.times(1))
                .deleteById(2L);

        Assertions.assertThrows(NotFoundException.class, () -> userService.getUserById(2L));

    }
}