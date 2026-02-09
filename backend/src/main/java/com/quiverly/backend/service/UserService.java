package com.quiverly.backend.service;

import com.quiverly.backend.exception.DuplicateEmailException;
import com.quiverly.backend.exception.DuplicateUsernameException;
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

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User with username " + username + " not found"));
    }

    public void addNewUser(User user) {
        Optional<User> userEmailOptional = userRepository.findUserByEmail(user.getEmail());
        if (userEmailOptional.isPresent()) {
            throw new DuplicateEmailException(user.getEmail());
        }

        Optional<User> userUsernameOptional = userRepository.findByUsername(user.getUsername());
        if (userUsernameOptional.isPresent()) {
            throw new DuplicateUsernameException(user.getUsername());
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

        if (username != null && !username.isEmpty() && !Objects.equals(user.getUsername(), username)) {
            Optional<User> userByUsername = userRepository.findByUsername(username);
            if (userByUsername.isPresent()) {
                throw new DuplicateUsernameException(username);
            }
            user.setUsername(username);
        }

        if (email != null && !email.isEmpty() && !Objects.equals(user.getEmail(), email)) {
            Optional<User> userByEmail = userRepository.findUserByEmail(email);
            if (userByEmail.isPresent()) {
                throw new DuplicateEmailException(email);
            }
            user.setEmail(email);
        }
    }
}
