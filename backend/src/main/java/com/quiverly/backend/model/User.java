package com.quiverly.backend.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table
public class User {
    @Id
    @GeneratedValue
    private Long id;
    private String username;
    //private String password;
    private String email;
    private LocalDateTime createdAt = LocalDateTime.now();

    //matches field in Surfboard that points to user
    //cascadeType ensures that surfboards dissapear when a user is deleted
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
    private List<Surfboard> surfboards = new ArrayList<>();

    public User(){

}

public User(Long id, String username, String email, LocalDateTime createdAt) {
    this.id = id;
    this.username = username;
    this.email = email;
    this.createdAt = createdAt;

}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}