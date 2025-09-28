package com.quiverly.backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

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