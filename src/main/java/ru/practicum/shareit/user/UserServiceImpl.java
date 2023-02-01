package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.AlreadyExistsException;

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
        isEmailExist(user);
        return userRepository.addNewUser(user).get();
    }

    @Override
    public User updateUser(User newUser, Long userId) {
        final User user = userRepository.findUserById(userId).get();
        isEmailExist(newUser);

        newUser.setId(userId);
        if(newUser.getName() == null) {
            newUser.setName(user.getName());
        }
        if(newUser.getEmail() == null) {
            newUser.setEmail(user.getEmail());
        }
        return userRepository.updateUser(newUser).get();
    }

    @Override
    public void deleteUser(Long userId) {
        userRepository.deleteUser(userId);
    }

    private void isEmailExist(User user) {
        boolean isExist = userRepository.findAllUsers()
                .stream()
                .map(User::getEmail)
                .anyMatch(
                        users -> users.equals(user.getEmail()));
        if(isExist) {
            throw new AlreadyExistsException(String.format("User email = '%s' already exists", user.getEmail()));
        }
    }
}
