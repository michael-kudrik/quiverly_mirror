package com.quiverly.backend.service;

import com.quiverly.backend.model.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
public class UserDetailsAdapter implements UserDetailsService {

    private final UserService userService;

    public UserDetailsAdapter(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // use your existing authenticate/find method
        User user = userService.getUsers().stream()
                .filter(u -> u.getUsername().equals(username) || u.getEmail().equals(username))
                .findFirst()
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities("USER") // simple role for now
                .build();
    }
}
