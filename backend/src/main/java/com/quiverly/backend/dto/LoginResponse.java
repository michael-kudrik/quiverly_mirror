package com.quiverly.backend.dto;

public class LoginResponse {
    private String token;
    private String username;

    @SuppressWarnings("unused")
    public LoginResponse() {
    }

    public LoginResponse(String token, String username) {
        this.token = token;
        this.username = username;
    }

    @SuppressWarnings("unused")
    public String getToken() {
        return token;
    }


    @SuppressWarnings("unused")
    public void setToken(String token) {
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
