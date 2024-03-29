package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.List;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User id = %d not found", userId)));
    }

    @Override
    @Transactional
    public User addUser(User user) {
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User updateUser(User newUser, Long userId) {
        final User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User id = %d not found", userId)));

        newUser.setId(userId);
        if (newUser.getName() == null) {
            newUser.setName(user.getName());
        }
        if (newUser.getEmail() == null) {
            newUser.setEmail(user.getEmail());
        }
        return userRepository.save(newUser);
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

}
