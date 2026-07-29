package com.quiverly.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class PasswordChangeRequest {

    private String currentPassword;


    public String getNewPassword() {
        return newPassword;
    }

    @SuppressWarnings("unused")
    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getCurrentPassword() {
        return currentPassword;
    }

    @SuppressWarnings("unused")
    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }


    @NotBlank
    @com.fasterxml.jackson.annotation.JsonProperty(access = com.fasterxml.jackson.annotation.JsonProperty.Access.WRITE_ONLY)
    @Size(min = 8, max = 64)
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[A-Z]).*$", message = "Password must have at least one uppercase letter and one number")
    private String newPassword;

    @SuppressWarnings("unused")
    public PasswordChangeRequest() {
    }

//    @SuppressWarnings("unused")
//    public PasswordChangeRequest(String currentPassword, String newPassword) {
//        this.currentPassword = currentPassword;
//        this.newPassword = newPassword;
//    }
}
