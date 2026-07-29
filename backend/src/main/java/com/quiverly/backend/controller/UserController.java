package com.quiverly.backend.controller;

import com.quiverly.backend.dto.PasswordChangeRequest;
import com.quiverly.backend.model.User;
import com.quiverly.backend.service.FileStorageService;
import com.quiverly.backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "/api/v1/user")
public class UserController {

    private final UserService userService;
    private final FileStorageService fileStorageService;

    @Autowired
    public UserController(UserService userService, FileStorageService fileStorageService) {
        this.userService = userService;
        this.fileStorageService = fileStorageService;
    }

    @GetMapping(path = "/me")
    public User getCurrentUser(Principal principal) {
        return userService.getUserByUsername(principal.getName());
    }

    @PostMapping(path = "/me/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> uploadAvatar(
            @RequestParam("file") MultipartFile file,
            Principal principal
    ) {
        String avatarUrl = userService.updateUserAvatar(principal.getName(), file, fileStorageService);
        return ResponseEntity.ok(Map.of("avatarUrl", avatarUrl));
    }

    @PutMapping(path = "/me/password")
    public ResponseEntity<Void> updatePassword(@Valid @RequestBody PasswordChangeRequest request, Principal principal) {
        userService.updateUserPassword(principal.getName(), request.getCurrentPassword(), request.getNewPassword());
        return ResponseEntity.noContent().build();
    }


    @DeleteMapping(path = "/me/avatar")
    public ResponseEntity<Void> deleteAvatar(Principal principal) {
        userService.removeUserAvatar(principal.getName(), fileStorageService);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public List<User> getUsers() {
        return userService.getUsers();
    }

    @PostMapping
    public void registerNewUser(@Valid @RequestBody User user) {
        userService.addNewUser(user);
    }

    @DeleteMapping(path = "{userId}")
    public void deleteUser(@PathVariable("userId") Long userId) {
        userService.deleteUser(userId);
    }

    @PutMapping(path = "{userId}")
    public void updateUser(@PathVariable("userId") Long userId,
                           @Valid @RequestBody com.quiverly.backend.dto.UserUpdateRequest request) {
        userService.updateUser(userId, request.getUsername(), request.getEmail());
    }
}
