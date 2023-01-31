package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAllUsers();
    }

    @Override
    public User getUserById(Long userId) {
        return userRepository.findUserById(userId).get();
    }

    @Override
    public User addUser(User user) {
        return userRepository.addNewUser(user).orElseThrow();
    }

    @Override
    public User updateUser(User user, Long userId) {
        userRepository.findUserById(userId);
        return userRepository.updateUser(user, userId).orElseThrow();
    }

    @Override
    public void deleteUser(Long userId) {
        userRepository.deleteUser(userId);
    }
}
