package com.quiverly.backend.service;

import com.quiverly.backend.exception.DuplicateEmailException;
import com.quiverly.backend.model.User;
import com.quiverly.backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> getUsers() {
        return userRepository.findAll();
    }

    public void addNewUser(User user) {
        Optional<User> userOptional = userRepository.findUserByEmail(user.getEmail()); //need to implement findUserByEmail query
        if (userOptional.isPresent()) {
            throw new DuplicateEmailException(user.getEmail());
        }
        if (user.getRole() == null) {
            user.setRole(User.Role.USER);
        }
        user.setPassword(passwordEncoder.encode(user.getPassword())); //encrypt password
        userRepository.save(user);
        System.out.println(user);
    }

    public void deleteUser(Long userId) {
        boolean exists = userRepository.existsById(userId);
        if (!exists) {
            throw new IllegalStateException("User with id: " + userId + " does not exist!");
        }
        userRepository.deleteById(userId);
    }

    @Transactional
    public void updateUser(Long userId, String username, String email) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalStateException("User with id: " + userId + " does not exist!"));

        if (username != null && !username.isEmpty() && !Objects.equals(user.getEmail(), email)) {
            Optional<User> userOptional = userRepository.findUserByEmail(email);
            if (userOptional.isPresent()) {
                throw new DuplicateEmailException(user.getEmail());
            }
            user.setEmail(email);
        }
    }
}
