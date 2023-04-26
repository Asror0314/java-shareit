package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{userId}")
    public User getUserById(@PathVariable final Long userId) {
        return userService.getUserById(userId);
    }

    @PostMapping
    public User addUser(@Valid @RequestBody final User user) {
        return userService.addUser(user);
    }

    @PatchMapping("/{userId}")
    public User updateUser(@RequestBody final User user, @PathVariable final Long userId) {
        return userService.updateUser(user, userId);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable final Long userId) {
        userService.deleteUser(userId);
    }
}
